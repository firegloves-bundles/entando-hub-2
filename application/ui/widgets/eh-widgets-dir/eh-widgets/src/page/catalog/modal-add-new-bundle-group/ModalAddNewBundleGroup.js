import { Button, Loading, Modal } from "carbon-components-react"
import { Add16 } from '@carbon/icons-react'
import ReactDOM from "react-dom"
import { useCallback, useEffect, useState } from "react"
import {
    addNewBundle,
    addNewBundleGroup,
    getAllCategories, getAllOrganisations
} from "../../../integration/Integration"
import './modal-add-new-bundle-group.scss'
import { bundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import { fillErrors } from "../../../helpers/validation/fillErrors"
import { getProfiledNewSelectStatusInfo } from "../../../helpers/profiling";
import { getHigherRole, isHubAdmin } from "../../../helpers/helpers";
import { getCurrentUserOrganisation } from "../../../integration/api-adapters";
import BundleGroupForm from "../../../components/forms/BundleGroupForm/BundleGroupForm";
import values from "../../../config/common-configuration";

/*
    This component manages the modal for adding a new bundle group
*/
export const ModalAddNewBundleGroup = ({ onAfterSubmit }) => {


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
        const [bundleErrorFunc, setBundleErrorFunc] = useState(null);

        const callOnAddBundleFunc = (bundleUrlErrorFunc) => {
            setBundleErrorFunc(() => bundleUrlErrorFunc);
        }

        const onDataChange = useCallback((bundleGroup) => {
            setBundleGroup(bundleGroup)
        }, [])

        const onRequestClose = (e) => {
            setOpen(false)
            setValidationResult({})
            resetData()
        }

        const onRequestOpen = (e) => {
            setOpen(true)
        }

        const resetData = () => {
            setElemKey(((new Date()).getTime()).toString())
        }

        useEffect(() => {
            setLoading(true)
            let isMounted = true
            const init = async () => {
                const categoryList = (await getAllCategories()).categoryList
                let localAllowedOrganisations
                if (!isHubAdmin()) {
                    localAllowedOrganisations = [(await getCurrentUserOrganisation())]
                } else {
                    localAllowedOrganisations = (await getAllOrganisations()).organisationList
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
                        description: "",
                        descriptionImage: values.bundleGroupForm.standardIcon,
                        documentationUrl: "",
                        children: [],
                        categories: [defaultCategoryId],
                        version: "",
                        status: "NOT_PUBLISHED",
                        organisationId: organizationId
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
            return toSend
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            //when submitting the form, the data to save are in newBundleGroup object
            (async () => {
                let validationError
                await bundleGroupSchema.validate(bundleGroup, { abortEarly: false }).catch(error => {
                    validationError = fillErrors(error)
                })
                let isChildGroupValidate = false;
                if (bundleGroup.children && bundleGroup.children.length === 0) {
                    isChildGroupValidate = true;
                    bundleErrorFunc();
                }
                if (validationError) {
                    setValidationResult(validationError)
                    return //don't send the form
                }
                if (isChildGroupValidate === true) {
                    return //don't send the form
                }
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
                            onBundleUrl={callOnAddBundleFunc}
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
                <Button onClick={onRequestOpen} renderIcon={Add16}>Add</Button>
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
    onBundleUrl
}) => {
    return (
        <>
            {loading && <Loading />}
            {!loading &&
                <Modal
                    className="Modal-Add-New-bundle-group"
                    modalLabel="Add"
                    primaryButtonText="Add"
                    secondaryButtonText="Cancel"
                    open={open}
                    onRequestClose={onRequestClose}
                    onRequestSubmit={onRequestSubmit}>
                    <BundleGroupForm key={elemKey} allowedOrganisations={allowedOrganisations} bundleGroup={bundleGroup}
                        categories={categories} selectStatusValues={selectStatusValues}
                        onDataChange={onDataChange} validationResult={validationResult} onBundleUrl={onBundleUrl} />
                </Modal>
            }        </>
    )
}
