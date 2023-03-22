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

import "./modal-update-bundle-group.scss"
import i18n from "../../../../i18n"
import BundleGroupForm from "../../../../components/forms/BundleGroupForm/BundleGroupForm"
import { useApiUrl } from "../../../../contexts/ConfigContext"
import { useCatalogs } from "../../../../contexts/CatalogContext"

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
  const [minOneBundleError] = useState("")

  const apiUrl = useApiUrl();

  const { catalogs } = useCatalogs();

  const isPublicOnly = catalogs.length === 0 || catalogs.every(({ organisationId }) => organisationId !== +bundleGroup.organisationId);

  useEffect(() => {
    setBundleGroup(prevBundleGroup => ({
      ...prevBundleGroup,
      publicCatalog: isPublicOnly
    }));
  }, [isPublicOnly]);

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
      const res = await getAllCategories(apiUrl)
      if (isMounted) {
        setCategories(res.categoryList)
      }
    }
    const initBG = async () => {
      const childrenFromDb =
        bundleGroupObj && bundleGroupObj.children && bundleGroupObj.children.length > 0
          ? (await getAllBundlesForABundleGroup(apiUrl, bundleGroupObj.bundleGroupVersionId)).bundleList
          : []

      const bundleGroupOrganisation = (await getSingleOrganisation(apiUrl, bundleGroupObj && bundleGroupObj.organisationId)).organisation

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
          publicCatalog: bg.publicCatalog,
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
  }, [apiUrl, bundleGroupId, bundleGroupObj])

  const updateBundleGroup = async (bundleGroup) => {

    const toSend = {
      ...bundleGroup,
    }

    if (bundleGroup.isEditable) {
      await editBundleGroup(apiUrl, toSend, toSend.bundleGroupId)
    } else {
      // Update payload for version update only
      toSend.versionDetails = {
        ...toSend.versionDetails,
        categories: toSend.categories,
        name: toSend.name
      }
      await editBundleGroupVersion(apiUrl, toSend.versionDetails, toSend.versionDetails.bundleGroupVersionId);
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
      if (validationError) {
        console.info("Form validation error(s)", validationError)
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
            isPublicOnly={isPublicOnly}
          />
        </Modal>
      }
    </>
  )
}
