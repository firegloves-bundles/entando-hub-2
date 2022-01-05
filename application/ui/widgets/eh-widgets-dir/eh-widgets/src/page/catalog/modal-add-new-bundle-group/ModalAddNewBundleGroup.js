import { Button, ComposedModal, Loading, ModalBody, ModalFooter, ModalHeader } from "carbon-components-react"
import { Add16 } from '@carbon/icons-react'
import ReactDOM from "react-dom"
import { useCallback, useEffect, useState } from "react"
import {
    addNewBundle,
    addNewBundleGroup,
} from "../../../integration/Integration"
import './modal-add-new-bundle-group.scss'
import { newBundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import { fillErrors } from "../../../helpers/validation/fillErrors"
import { getProfiledNewSelectStatusInfo } from "../../../helpers/profiling";
import { getHigherRole, isHubAdmin } from "../../../helpers/helpers";
import BundleGroupForm from "../../../components/forms/BundleGroupForm/BundleGroupForm";
import values from "../../../config/common-configuration";
import { BUNDLE_STATUS } from "../../../helpers/constants";
import i18n from "../../../i18n"

/*
    This component manages the modal for adding a new bundle group
*/
export const ModalAddNewBundleGroup = ({ onAfterSubmit, catList, orgList, currentUserOrg}) => {


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
                const filtered = categories && categories.filter(cat => cat.name === "Solution Template")
                if (filtered) {
                    defaultCategoryId = (filtered.length > 0) ? filtered[0].categoryId : categories[0]
                }
            }
            const organizationId = (allowedOrganisations && allowedOrganisations.length > 0) ? allowedOrganisations[0].organisationId : null;
            setBundleGroup(
                {
                    name: "",
                    children: [],
                    categories: [defaultCategoryId],
                    organisationId: organizationId,
                    versionDetails: {
                        bundleGroupVersionId: null,
                        description: "",
                        descriptionImage: values.bundleGroupForm.standardIcon,
                        documentationUrl: "",
                        bundleGroupUrl: "",
                        version: "",
                        status: "NOT_PUBLISHED",
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
                        const filtered = categoryList && categoryList.filter(cat => cat.name === "Solution Template")
                        if (filtered) {
                            defaultCategoryId = (filtered.length > 0) ? filtered[0].categoryId : categoryList[0]
                        }
                    }
                    const organizationId = (localAllowedOrganisations && localAllowedOrganisations.length > 0) ? localAllowedOrganisations[0].organisationId : null;

                    const newObj = {
                        name: "",
                        children: [],
                        categories: [defaultCategoryId],
                        organisationId: organizationId,
                        versionDetails: {
                            bundleGroupVersionId: null,
                            description: "",
                            descriptionImage: values.bundleGroupForm.standardIcon,
                            documentationUrl: "",
                            bundleGroupUrl: "",
                            version: "",
                            status: "NOT_PUBLISHED",
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


        //TODO BE QUERY REFACTORING
        const createNewBundleGroup = async (bundleGroup) => {
            let newChildren = [] //children are the bundles
            if (bundleGroup.children && bundleGroup.children.length) {
                //call addNewBundle rest api, saving every bundle
                //the call is async in respArray there will be the new bundles id
                let respArray = await Promise.all(bundleGroup.children.map(addNewBundle)) //addNewBundle creates a new bundle in the DB
                //new children will be an array of bundle ids
                newChildren = respArray.map(res => res.newBundle.data.bundleId)
            }
            //build a new bundleGroup object with only the ids in the children array
            const toSend = {
                ...bundleGroup,
                children: newChildren
            }
            await addNewBundleGroup(toSend)
            setReqOnWay(false);
            return toSend
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            //when submitting the form, the data to save are in newBundleGroup object
            (async () => {
                let validationError
                await newBundleGroupSchema.validate(bundleGroup, { abortEarly: false }).catch(error => {
                    validationError = fillErrors(error)
                })
                // bypass the validation for Draft(NOT_PUBLISHED) Status.
                if (bundleGroup.versionDetails.status === BUNDLE_STATUS.NOT_PUBLISHED &&
                    validationError && validationError.children && validationError.children.length === 1 &&
                    Object.keys(validationError).length === 1) {
                    validationError = undefined;
                }

                if (bundleGroup.children && bundleGroup.children.length === 0 &&
                    bundleGroup.versionDetails.status !== BUNDLE_STATUS.NOT_PUBLISHED) {
                    setMinOneBundleError(validationError.children[0]);
                }
                if (validationError) {
                    setValidationResult(validationError)
                    return //don't send the form
                }
                setReqOnWay(true);
                const toSend = await createNewBundleGroup(bundleGroup)
                //WARNING type changed: children (bundle) in new bundle group after the update contains only the id
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
    reqOnWay
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
                            onDataChange={onDataChange} validationResult={validationResult} minOneBundleError={minOneBundleError} />
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
