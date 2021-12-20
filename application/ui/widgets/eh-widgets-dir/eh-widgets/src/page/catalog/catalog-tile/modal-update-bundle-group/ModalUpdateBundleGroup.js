import { Loading, Modal } from "carbon-components-react"
import BundleGroupForm from "../../../../components/forms/BundleGroupForm/BundleGroupForm"
import { useCallback, useEffect, useState } from "react"
import {
  addNewBundle,
  editBundleGroup,
  getAllBundlesForABundleGroup,
  getAllCategories,
  getSingleBundleGroup,
  getSingleOrganisation,
} from "../../../../integration/Integration"
import { getProfiledUpdateSelectStatusInfo } from "../../../../helpers/profiling"
import { getHigherRole } from "../../../../helpers/helpers"
import {
  bundleGroupSchema,
} from "../../../../helpers/validation/bundleGroupSchema"
import { fillErrors } from "../../../../helpers/validation/fillErrors"
import { BUNDLE_STATUS } from "../../../../helpers/constants"

import "./modal-update-bundle-group.scss"
import i18n from "../../../../i18n"

export const ModalUpdateBundleGroup = ({
  bundleGroupId,
  bundleStatus,
  open,
  onCloseModal,
  onAfterSubmit,
}) => {
  const [allowedOrganisations, setAllowedOrganisations] = useState([{
    organisationId: "",
    name: "",
  }])
  const [categories, setCategories] = useState([])

  const [bundleGroup, setBundleGroup] = useState({})
  const [passiveModal, setPassiveModal] = useState(false)
  const [loading, setLoading] = useState(true)

  const [selectStatusValues, setSelectStatusValues] = useState([])
  const [validationResult, setValidationResult] = useState({})
  const [minOneBundleError, setMinOneBundleError] = useState("")


  const onDataChange = useCallback((bundleGroup) => {
    setBundleGroup(bundleGroup)
  }, [])

  const onRequestClose = (e) => {
    onCloseModal()
    setValidationResult({})
  }

  useEffect(() => {
    let isMounted = true
    setLoading(true)

    const initCG = async () => {
      const res = await getAllCategories()
      if (isMounted) {
        setCategories(res.categoryList)
      }
    }
    const initBG = async () => {
      const res = await getSingleBundleGroup(bundleGroupId)
      const childrenFromDb =
      res && res.bundleGroup && res.bundleGroup.children && res.bundleGroup.children.length > 0
          ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList
          : []

      const bundleGroupOrganisation = (
        await getSingleOrganisation(res && res.bundleGroup && res.bundleGroup.organisationId)
      ).organisation
      if (isMounted) {
        if (bundleGroupOrganisation) {
          setAllowedOrganisations([bundleGroupOrganisation])
        }
        let bg = {
          ...res.bundleGroup,
          children: childrenFromDb,
        }
        const selectStatusValues = getProfiledUpdateSelectStatusInfo(
          getHigherRole(),
          bg.status
        )
        setSelectStatusValues(selectStatusValues)
        setPassiveModal(selectStatusValues.disabled)
        setBundleGroup(bg)
      }
    }

    ;(async () => {
      await Promise.all([initCG(), initBG()])
      setLoading(false)
    })()
    return () => {
      isMounted = false
    }
  }, [bundleGroupId])

  //TODO BE QUERY REFACTORING
  const updateBundleGroup = async (bundleGroup) => {
    let newChildren = []
    if (bundleGroup.children && bundleGroup.children.length) {
      //call addNewBundle rest api, saving every bundle
      //WARNING a new bundle is created even if already exists
      //the call is async in respArray there will be the new bundles id
      let respArray = await Promise.all(bundleGroup.children.map(addNewBundle))
      newChildren = respArray.map((res) => res && res.newBundle && res.newBundle.data && res.newBundle.data.bundleId)
    }
    const toSend = {
      ...bundleGroup,
      children: newChildren,
    }
    await editBundleGroup(toSend, toSend.bundleGroupId)
  }
  
  const onRequestSubmit = (e) => {
    ;(async () => {
      let validationError
      await bundleGroupSchema
      .validate(bundleGroup, { abortEarly: false })
      .catch((err) => {
        validationError = fillErrors(err)
      })
      if ((bundleGroup && (bundleGroup.status === BUNDLE_STATUS.NOT_PUBLISHED || bundleGroup.status === BUNDLE_STATUS.DELETE_REQ)) &&
          validationError && validationError.children && validationError.children.length === 1 &&
          Object.keys(validationError).length === 1) {
          validationError = undefined;
      }
      if (bundleGroup && bundleGroup && bundleGroup.children && bundleGroup.children.length === 0 &&
        (bundleGroup.status === BUNDLE_STATUS.PUBLISH_REQ || bundleGroup.status === BUNDLE_STATUS.PUBLISHED)) {
        setMinOneBundleError(validationError.children[0]);
      }
      if (validationError) {
        setValidationResult(validationError)
        return
      }
      await updateBundleGroup(bundleGroup)
      onCloseModal()
      onAfterSubmit()
    })()
  }

  return (
    <>
      {loading && <Loading />}
      {!loading &&
        <Modal
          passiveModal={passiveModal}
          className="Modal-edit-bundle-group"
          modalLabel={i18n.t('component.button.edit')}
          primaryButtonText={i18n.t('component.button.save')}
          secondaryButtonText={i18n.t('component.button.cancel')}
          open={open}
          onRequestClose={onRequestClose}
          onRequestSubmit={onRequestSubmit}
        >
          <BundleGroupForm
            mode="Edit"
            allowedOrganisations={allowedOrganisations}
            categories={categories}
            onDataChange={onDataChange}
            bundleGroup={bundleGroup}
            theBundleStatus={bundleStatus}
            selectStatusValues={selectStatusValues}
            validationResult={validationResult}
            minOneBundleError={minOneBundleError}
          />
        </Modal>
      }
    </>
  )
}
