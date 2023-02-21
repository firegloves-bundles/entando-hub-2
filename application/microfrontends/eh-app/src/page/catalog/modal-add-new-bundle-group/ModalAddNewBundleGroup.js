import { Button, ComposedModal, Loading, ModalBody, ModalFooter, ModalHeader } from "carbon-components-react"
import { Add16 } from '@carbon/icons-react'
import ReactDOM from "react-dom"
import { useCallback, useEffect, useState } from "react"
import { addNewBundleGroup } from "../../../integration/Integration"
import './modal-add-new-bundle-group.scss'
import { newBundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import { fillErrors } from "../../../helpers/validation/fillErrors"
import { getProfiledNewSelectStatusInfo } from "../../../helpers/profiling";
import { getHigherRole, isHubAdmin } from "../../../helpers/helpers";
import BundleGroupForm from "../../../components/forms/BundleGroupForm/BundleGroupForm";
import values from "../../../config/common-configuration";
import { DEFAULT_CATEGORY, BUNDLE_STATUS } from "../../../helpers/constants";
import i18n from "../../../i18n"
import { useApiUrl } from "../../../contexts/ConfigContext"

/*
    This component manages the modal for adding a new bundle group
*/
export const ModalAddNewBundleGroup = ({ onAfterSubmit, catList, orgList, currentUserOrg}) => {
    const apiUrl = useApiUrl();

    const ModalStateManager = ({
        renderLauncher: LauncherContent,
        children: ModalContent,
    }) => {
        const [open, setOpen] = useState(false)
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data

        const [allowedOrganisations, setAllowedOrganisations] = useState([{
            organisationId: "",
            name: "",
        }])
        const [categories, setCategories] = useState([])
        const [bundleGroup, setBundleGroup] = useState({})
        const [loading, setLoading] = useState(true)
        const [selectStatusValues, setSelectStatusValues] = useState([])
        const [validationResult, setValidationResult] = useState({})
        const [minOneBundleError, setMinOneBundleError] = useState("")
        const [reqOnWay, setReqOnWay] = useState(false)

        const onDataChange = useCallback((bundleGroup) => {
            setBundleGroup(bundleGroup)
        }, [])

        const resetData = () => {
            setElemKey(((new Date()).getTime()).toString())
            // Reset Form
            let defaultCategoryId = null;
            if (categories) {
                let filtered = categories && categories.filter(cat => cat.name === DEFAULT_CATEGORY)
                if (!filtered.length) {
                    filtered = categories && categories.filter(cat => cat.name === catList[0].name)
                }
                if (filtered) {
                    defaultCategoryId = (filtered.length > 0) ? filtered[0].categoryId : categories[0]
                }
            }
            const { organisationId, catalogId } = (allowedOrganisations && allowedOrganisations.length > 0) ? allowedOrganisations[0] : {};

            setBundleGroup(
                {
                    name: "",
                    categories: [defaultCategoryId],
                    organisationId,
                    catalogId,
                    versionDetails: {
                        bundleGroupVersionId: null,
                        description: "",
                        descriptionImage: values.bundleGroupForm.standardIcon,
                        documentationUrl: "",
                        displayContactUrl: false,
                        contactUrl: "",
                        version: "",
                        status: "NOT_PUBLISHED",
                        bundles: []
                    }
                }
            )
        }

        const onRequestClose = (e) => {
            setOpen(false)
            setValidationResult({})
            resetData()
        }

        const onRequestOpen = (e) => {
            setOpen(true)
        }

        useEffect(() => {
            setLoading(true)
            let isMounted = true
            const init = async () => {
                const categoryList = catList;
                let localAllowedOrganisations
                if (!isHubAdmin()) {
                    const currentUserOrganisation = currentUserOrg;
                    localAllowedOrganisations = currentUserOrganisation ? [currentUserOrganisation] : [];
                } else {
                    localAllowedOrganisations = orgList
                }
                const selectStatusValues = getProfiledNewSelectStatusInfo(getHigherRole())
                if (isMounted) {
                    setCategories(categoryList)
                    setSelectStatusValues(selectStatusValues)
                    setAllowedOrganisations(localAllowedOrganisations)
                    //default values
                    let defaultCategoryId = null;
                    if (categoryList) {
                        let filtered = categoryList && categoryList.filter(cat => cat.name === DEFAULT_CATEGORY)
                        if (!filtered.length) {
                            filtered = categoryList && categoryList.filter(cat => cat.name === catList[0].name)
                        }
                        if (filtered) {
                            defaultCategoryId = (filtered.length > 0) ? filtered[0].categoryId : categoryList[0]
                        }
                    }
                    const { organisationId, catalogId } = (localAllowedOrganisations && localAllowedOrganisations.length > 0) ? localAllowedOrganisations[0] : {};

                    const newObj = {
                        name: "",
                        categories: [defaultCategoryId],
                        organisationId,
                        catalogId,
                        isPublic: false,
                        versionDetails: {
                            bundleGroupVersionId: null,
                            description: "",
                            descriptionImage: values.bundleGroupForm.standardIcon,
                            documentationUrl: "",
                            displayContactUrl: false,
                            contactUrl: "",
                            version: "",
                            status: "NOT_PUBLISHED",
                            bundles: []
                        }
                    }

                    setBundleGroup(newObj)
                    setLoading(false)
                }
            }
            init()
            return () => {
                isMounted = false
            }
        }, [])

        const createNewBundleGroup = async (bundleGroup) => {
            const toSend = {
                ...bundleGroup,
            }
            await addNewBundleGroup(apiUrl, toSend)
            setReqOnWay(false);
            return toSend
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            (async () => {
                let validationError
                await newBundleGroupSchema
                    .validate(bundleGroup, { abortEarly: false })
                    .catch(error => {
                        validationError = fillErrors(error)
                })
                const details = bundleGroup.versionDetails;
                if (details.bundles && details.bundles.length === 0 &&
                    (details.displayContactUrl !== true) &&
                    details.status !== BUNDLE_STATUS.NOT_PUBLISHED) {
                    validationError && setMinOneBundleError(validationError['versionDetails.bundles'][0]);
                }
                if (validationError) {
                    console.info("Form validation error(s)", validationError)
                    setValidationResult(validationError)
                    return //don't send the form
                }
                setReqOnWay(true);
                const toSend = await createNewBundleGroup(bundleGroup)
                setBundleGroup(toSend)
                resetData()
                onAfterSubmit()
            })()
        }
        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent open={open}
                            onRequestClose={onRequestClose}
                            onDataChange={onDataChange}
                            onRequestSubmit={onRequestSubmit}
                            elemKey={elemKey}
                            validationResult={validationResult}
                            allowedOrganisations={allowedOrganisations}
                            categories={categories}
                            selectStatusValues={selectStatusValues}
                            bundleGroup={bundleGroup}
                            loading={loading}
                            minOneBundleError={minOneBundleError}
                            reqOnWay={reqOnWay}
                            orgList={orgList}
                        />,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen} />}
            </>
        )
    }

    return (
        <ModalStateManager
            renderLauncher={({ onRequestOpen }) => (
                <Button onClick={onRequestOpen} renderIcon={Add16}>{i18n.t('component.button.add')}</Button>
            )}>
            {ModalContent}
        </ModalStateManager>
    )
}

const ModalContent = ({
    open,
    onRequestClose,
    onDataChange,
    onRequestSubmit,
    elemKey,
    validationResult,
    bundleGroup,
    selectStatusValues,
    allowedOrganisations,
    categories,
    loading,
    minOneBundleError,
    reqOnWay,
    orgList
}) => {
    return (
        <>
            {loading && <Loading />}
            {!loading &&
                <ComposedModal
                    className="Modal-Add-New-bundle-group"
                    open={open}
                    onClose={onRequestClose}
                >
                    <ModalHeader label={i18n.t('component.button.add')} />
                    <ModalBody>
                        <BundleGroupForm mode="Add" key={elemKey} allowedOrganisations={allowedOrganisations} bundleGroup={bundleGroup}
                            categories={categories} selectStatusValues={selectStatusValues}
                            onDataChange={onDataChange} validationResult={validationResult} minOneBundleError={minOneBundleError} orgList={orgList}/>
                    </ModalBody>
                    <ModalFooter>
                        <Button
                            kind="secondary"
                            onMouseDown={() => { onRequestClose() }}>
                            {i18n.t('component.button.cancel')}
                        </Button>
                        <Button
                            kind="primary" disabled={reqOnWay}
                            onClick={() => { onRequestSubmit() }}>
                            {i18n.t('component.button.add')}
                        </Button>
                    </ModalFooter>
                </ComposedModal>
            }
        </>
    )
}
