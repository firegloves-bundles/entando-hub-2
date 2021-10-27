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
  fillErrors,
} from "../../../../helpers/validation/bundleGroupSchema"
import { fireEvent, SUCCESS, FAIL } from "../../../../helpers/eventDispatcher"

import "./modal-update-bundle-group.scss"

export const ModalUpdateBundleGroup = ({
  bundleGroupId,
  open,
  onCloseModal,
  onAfterSubmit,
}) => {
  const [organisation, setOrganisation] = useState({
    organisationId: "",
    name: "",
  })
  const [categories, setCategories] = useState([])

  const [bundleGroup, setBundleGroup] = useState({})
  const [passiveModal, setPassiveModal] = useState(false)
  const [loading, setLoading] = useState(true)

  const [selectStatusValues, setSelectStatusValues] = useState([])
  const [validationResult, setValidationResult] = useState({})

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
        res.bundleGroup.children && res.bundleGroup.children.length > 0
          ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList
          : []

      const organisation = (
        await getSingleOrganisation(res.bundleGroup.organisationId)
      ).organisation
      if (isMounted) {
        if (organisation) {
          setOrganisation(organisation)
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
      newChildren = respArray.map((res) => res.newBundle.data.bundleId)
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
          modalLabel="Edit"
          primaryButtonText="Save"
          secondaryButtonText="Cancel"
          open={open}
          onRequestClose={onRequestClose}
          onRequestSubmit={onRequestSubmit}
        >
          <BundleGroupForm
            organisation={organisation}
            categories={categories}
            onDataChange={onDataChange}
            bundleGroup={bundleGroup}
            selectStatusValues={selectStatusValues}
            validationResult={validationResult}
          />
        </Modal>
      }
    </>
  )
}
