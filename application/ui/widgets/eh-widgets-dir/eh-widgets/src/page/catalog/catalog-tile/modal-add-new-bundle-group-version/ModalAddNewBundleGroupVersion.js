import { Loading, Modal } from "carbon-components-react"
import { useCallback, useEffect, useState } from "react"
import {
  addNewBundleGroupVersion,
  getAllBundlesForABundleGroup,
  getAllCategories,
  getSingleOrganisation,
} from "../../../../integration/Integration"
import { getProfiledNewSelectStatusInfo } from "../../../../helpers/profiling"
import { getHigherRole } from "../../../../helpers/helpers"
import {
  versionBundleGroupSchema,
} from "../../../../helpers/validation/bundleGroupSchema"
import { fillErrors } from "../../../../helpers/validation/fillErrors"
import { BUNDLE_STATUS } from "../../../../helpers/constants"

import "./modal-add-new-bundle-group-version.scss"
import BundleGroupVersionForm from "../../../../components/forms/BundleGroupVersionForm/BundleGroupVersionForm"
import i18n from "../../../../i18n"
import { isVersionDuplicate } from "../../../../helpers/validation/isVersionDuplicateValidate"

export const ModalAddNewBundleGroupVersion = ({
  theBundleGroup,
  open,
  onCloseModal,
  onAfterSubmit,
  operation
}) => {
  const [allowedOrganisations, setAllowedOrganisations] = useState([{
    organisationId: "",
    name: "",
  }])
  const [categories, setCategories] = useState([])

  const [bundleGroup, setBundleGroup] = useState({})
  const [loading, setLoading] = useState(true)

  const [selectStatusValues, setSelectStatusValues] = useState([])
  const [validationResult, setValidationResult] = useState({})
  const [minOneBundleError] = useState("")


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
      const childrenFromDb = theBundleGroup 
                              && theBundleGroup.children 
                              && theBundleGroup.children.length > 0 
                              && theBundleGroup.bundleGroupId
          ? (await getAllBundlesForABundleGroup(theBundleGroup.bundleGroupVersionId)).bundleList
          : [];
          
        const bundleGroupOrganisation = (await getSingleOrganisation(theBundleGroup && theBundleGroup.organisationId)).organisation;
        if (isMounted) {
          if (bundleGroupOrganisation) {
            setAllowedOrganisations([bundleGroupOrganisation])
          }
          let bg = {
            ...theBundleGroup,
            bundles: childrenFromDb,
            status: BUNDLE_STATUS.NOT_PUBLISHED
          }
          const selectStatusValues = getProfiledNewSelectStatusInfo(getHigherRole())
          setSelectStatusValues(selectStatusValues);
          setBundleGroup(bg);
        }
    }

    ;(async () => {
      await Promise.all([initCG(), initBG()])
      setLoading(false)
    })()
    return () => {
      isMounted = false
    }
  }, [theBundleGroup])

  //Add Bundle Group Version api call
  const addBundleGroupVersion = async(bundleGroupVersion) => {
    
    const toSend = {
      ...bundleGroup,
    }
    await addNewBundleGroupVersion(toSend);
  }

  const onRequestSubmit = (e) => {
    
    ;(async () => {
      let validationError
      await versionBundleGroupSchema.validate(bundleGroup, { abortEarly: false })
        .catch((err) => {
          validationError = fillErrors(err)
        })
      if (isVersionDuplicate(bundleGroup.version, theBundleGroup.allVersions)) {
        let versionValidationError = (validationError && validationError.version) ? [...validationError.version, i18n.t('formValidationMsg.duplicateVersion')] : [i18n.t('formValidationMsg.duplicateVersion')]
        if (!validationError) {
          validationError = { version: versionValidationError }
        }
        validationError.version = versionValidationError
      }

      if (validationError) {
        setValidationResult(validationError)
        return
      }
      await addBundleGroupVersion(bundleGroup)
      onCloseModal()
      onAfterSubmit()
    })()
  }

  return (
    <>
      {loading && <Loading />}
      {!loading &&
        <Modal
          className="Modal-edit-bundle-group"
          modalLabel={i18n.t('modalMsg.addNewVersion')}
          primaryButtonText={i18n.t('component.button.submit')}
          secondaryButtonText={i18n.t('component.button.cancel')}
          open={open}
          onRequestClose={onRequestClose}
          onRequestSubmit={onRequestSubmit}
        >
          <BundleGroupVersionForm
            allowedOrganisations={allowedOrganisations}
            categories={categories}
            onDataChange={onDataChange}
            bundleGroup={bundleGroup}
            selectStatusValues={selectStatusValues}
            validationResult={validationResult}
            minOneBundleError={minOneBundleError}
            allVersions={theBundleGroup.allVersions}
            mode="Edit"
            operation={operation}
          />
        </Modal>
      }
    </>
  )
}