import {Button, Loading, Modal} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import ReactDOM from "react-dom"
import {useCallback, useEffect, useState} from "react"
import {
    addNewBundle,
    addNewBundleGroup,
    getAllCategories
} from "../../../integration/Integration"
import './modal-add-new-bundle-group.scss'
import {bundleGroupSchema, fillErrors} from "../../../helpers/validation/bundleGroupSchema";
import {getProfiledNewSelecSatustInfo} from "../../../helpers/profiling";
import {getHigherRole} from "../../../helpers/helpers";
import {getCurrentUserOrganisation} from "../../../integration/api-adapters";
import BundleGroupForm from "../../../components/forms/BundleGroupForm/BundleGroupForm";
import values from "../../../config/common-configuration";
import { fireEvent, SUCCESS, FAIL } from "../../../helpers/eventDispatcher"


/*
    TODO code cut and pasted from the crabon documentation can be semplified
    This component manages the modal for adding a new bundle group
*/

export const ModalAddNewBundleGroup = ({onAfterSubmit}) => {


    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data

        const [organisation, setOrganisation] = useState({
            organisationId: "",
            name: "",
        })
        const [categories, setCategories] = useState([])
        const [bundleGroup, setBundleGroup] = useState({})
        const [loading, setLoading] = useState(true)
        const [selectStatusValues, setSelectStatusValues] = useState([])
        const [validationResult, setValidationResult] = useState({})


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
                const userOrganisation = await getCurrentUserOrganisation()
                const selectStatusValues = getProfiledNewSelecSatustInfo(getHigherRole())
                if (isMounted) {
                    setCategories(categoryList)
                    setSelectStatusValues(selectStatusValues)
                    if (userOrganisation) setOrganisation(userOrganisation)
                    //default values
                    const filtered = categoryList.filter(cat => cat.name === "Solution Template")
                    let defaultCategoryId = (filtered.length > 0) ? filtered[0].categoryId : categoryList[0]
                    const newObj = {
                        name: "",
                        description: "",
                        descriptionImage: values.bundleGroupForm.standardIcon,
                        documentationUrl: "",
                        children: [],
                        categories: [defaultCategoryId],
                        version: "",
                        status: "NOT_PUBLISHED",
                        organisationId: userOrganisation ? userOrganisation.organisationId : undefined
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
            const bg = await addNewBundleGroup(toSend)
            if (bg.isError) {
                fireEvent(FAIL, `Impossible to create bundle group: ${bg.errorBody.message}`)
            } else {
                fireEvent(SUCCESS, `Bundle group ${bg.newBundleGroup.data.name} created`)
            }
            return toSend
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            //when submitting the form, the data to save are in newBundleGroup object

            (async () => {
                let validationError
                await bundleGroupSchema.validate(bundleGroup, {abortEarly: false}).catch(error => {
                    validationError = fillErrors(error)
                })
                if (validationError) {
                    setValidationResult(validationError)
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
                                      organisation={organisation}
                                      categories={categories}
                                      selectStatusValues={selectStatusValues}
                                      bundleGroup={bundleGroup}
                                      loading={loading}
                        />,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }


    return (
        <ModalStateManager
            renderLauncher={({onRequestOpen}) => (
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
                          organisation,
                          categories,
                          loading
                      }) => {
    return (
        <>
            {loading && <Loading/>}
            {!loading &&
            <Modal
                className="Modal-Add-New-bundle-group"
                modalLabel="Add"
                primaryButtonText="Add"
                secondaryButtonText="Cancel"
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestSubmit}>
                <BundleGroupForm key={elemKey} organisation={organisation} bundleGroup={bundleGroup}
                                 categories={categories} selectStatusValues={selectStatusValues}
                                 onDataChange={onDataChange} validationResult={validationResult}/>
            </Modal>
            }        </>
    )
}
