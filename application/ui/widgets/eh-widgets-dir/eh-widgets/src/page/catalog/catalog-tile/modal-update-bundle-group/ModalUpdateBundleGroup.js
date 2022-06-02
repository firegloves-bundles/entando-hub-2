import { Loading, Modal } from "carbon-components-react"
import { useCallback, useEffect, useState } from "react"
import {
  editBundleGroup,
  editBundleGroupVersion,
  getAllBundlesForABundleGroup,
  getAllCategories,
  getSingleOrganisation,
} from "../../../../integration/Integration"
import { getProfiledUpdateSelectStatusInfo } from "../../../../helpers/profiling"
import { getHigherRole } from "../../../../helpers/helpers"
import { newBundleGroupSchema } from "../../../../helpers/validation/bundleGroupSchema"
import { fillErrors } from "../../../../helpers/validation/fillErrors"
import { BUNDLE_STATUS } from "../../../../helpers/constants"

import "./modal-update-bundle-group.scss"
import i18n from "../../../../i18n"
import BundleGroupForm from "../../../../components/forms/BundleGroupForm/BundleGroupForm"

export const ModalUpdateBundleGroup = ({
  bundleGroupId,
  bundleStatus,
  open,
  onCloseModal,
  onAfterSubmit,
  bundleGroupObj,
  orgList
}) => {
  
  const [allowedOrganisations, setAllowedOrganisations] = useState([{
    organisationId: "",
    name: "",
  }])
  const [categories, setCategories] = useState([])

  const [bundleGroup, setBundleGroup] = useState(bundleGroupObj)
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
      const childrenFromDb =
        bundleGroupObj && bundleGroupObj.children && bundleGroupObj.children.length > 0
          ? (await getAllBundlesForABundleGroup(bundleGroupObj.bundleGroupVersionId)).bundleList
          : []

      const bundleGroupOrganisation = (await getSingleOrganisation(bundleGroupObj && bundleGroupObj.organisationId)).organisation

      if (isMounted) {
        if (bundleGroupOrganisation) {
          setAllowedOrganisations([bundleGroupOrganisation])
        }
        let bg = {
          ...bundleGroupObj,
        }
        const selectStatusValues = getProfiledUpdateSelectStatusInfo(
          getHigherRole(),
          bg.status
        )
        setSelectStatusValues(selectStatusValues)
        setPassiveModal(selectStatusValues.disabled);

        /**
         * Prepare required bundle group object to show prefilled values on form
         */
        let newObject = {
          bundleGroupId: bg.bundleGroupId,
          name: bg.name,
          categories: bg.categories,
          organisationId: bg.organisationId,
          isEditable: bg.isEditable,
          versionDetails: {
            bundleGroupVersionId: bg.bundleGroupVersionId,
            description: bg.description,
            descriptionImage: bg.descriptionImage,
            documentationUrl: bg.documentationUrl,
            displayContactUrl: bg.displayContactUrl,
            contactUrl: bg.contactUrl,
            bundleGroupUrl: bg.bundleGroupUrl,
            version: bg.version,
            status: bg.status,
            bundles: childrenFromDb
          }
        }
        setBundleGroup(newObject);
      }
    }

    ;(async () => {
      await Promise.all([initCG(), initBG()])
      setLoading(false)
    })()
    return () => {
      isMounted = false
    }
  }, [bundleGroupId, bundleGroupObj])

  //TODO BE QUERY REFACTORING
  const updateBundleGroup = async (bundleGroup) => {
    
    const toSend = {
      ...bundleGroup,
    }
    
    if (bundleGroup.isEditable) {
      await editBundleGroup(toSend, toSend.bundleGroupId)
    } else {
      // Update payload for version update only
      toSend.versionDetails = {
        ...toSend.versionDetails,
        categories: toSend.categories,
        name: toSend.name
      }
      await editBundleGroupVersion(toSend.versionDetails, toSend.versionDetails.bundleGroupVersionId);
    }
  }
  
  const onRequestSubmit = (e) => {
    ;(async () => {
      let validationError
      await newBundleGroupSchema
      .validate(bundleGroup, { abortEarly: false })
      .catch((err) => {
        validationError = fillErrors(err)
      })
      if ((bundleGroup && (bundleGroup.versionDetails.status === BUNDLE_STATUS.NOT_PUBLISHED || bundleGroup.versionDetails.status === BUNDLE_STATUS.DELETE_REQ)) &&
          validationError && validationError['versionDetails.bundles'] && validationError['versionDetails.bundles'].length === 1 &&
          Object.keys(validationError).length === 1) {
          validationError = undefined;
      }
      if (bundleGroup && bundleGroup.bundles && bundleGroup.bundles.length === 0 &&
        (bundleGroup.versionDetails.status === BUNDLE_STATUS.PUBLISH_REQ || bundleGroup.versionDetails.status === BUNDLE_STATUS.PUBLISHED)) {
        setMinOneBundleError(validationError['versionDetails.bundles'][0]);
      }
      if (validationError) {
        setValidationResult(validationError)
        return //don't send the form
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
            orgList={orgList}
          />
        </Modal>
      }
    </>
  )
}
