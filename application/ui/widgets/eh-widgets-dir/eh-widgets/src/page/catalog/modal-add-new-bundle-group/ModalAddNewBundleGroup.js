import {Button, Modal} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import ReactDOM from "react-dom"
import {useState} from "react"
import NewBundleGroup from "./new-boundle-group/NewBundleGroup"
import {addNewBundle, addNewBundleGroup} from "../../../integration/Integration"


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

        //Warning newBundleGroup will contain the entire bundle info in the children array, not only the bundle id
        const [newBundleGroup, setNewBundleGroup] = useState({})

        const onDataChange = (newBundleGroup)=>{
            setNewBundleGroup(newBundleGroup)
        }

        const onRequestClose = (e) =>{
            setOpen(false)
            resetData()
        }

        const onRequestOpen = (e) =>{
            setOpen(true)
        }

        const resetData = ()=>{
            setElemKey(((new Date()).getTime()).toString())
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            e.preventDefault(); //TODO check if needed
            //when submitting the form, the data to save are in newBundleGroup object
            (async () => {
                //create bundle children
                let newChildren = [] //children are the bundles
                if(newBundleGroup.children && newBundleGroup.children.length) {
                    //call addNewBundle rest api, saving every bundle
                    //the call is async in respArray there will be the new bundles id
                    let respArray = await Promise.all(newBundleGroup.children.map(addNewBundle))
                    //new children will be an array of bundle ids
                    newChildren = respArray.map(res => res.newBundle.data.bundleId)
                }
                //build a new bundleGroup object with only the ids in the children array
                const toSend = {
                    ...newBundleGroup,
                    children: newChildren
                }
                const res = await addNewBundleGroup(toSend)
                console.log("addNewBundleGroup", res)
                setNewBundleGroup(toSend)
                resetData()
                onAfterSubmit()

            })()
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent open={open} onRequestClose={onRequestClose} onDataChange={onDataChange} onRequestSubmit={onRequestSubmit} elemKey={elemKey}/>,
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
            {({open, onRequestClose, onDataChange, onRequestSubmit, elemKey}) => (
                <Modal
                    modalLabel="Add"
                    primaryButtonText="Add"
                    secondaryButtonText="Cancel"
                    open={open}
                    onRequestClose={onRequestClose}
                    onRequestSubmit={onRequestSubmit}>
                    <NewBundleGroup key={elemKey} onDataChange={onDataChange}/>
                </Modal>
            )}
        </ModalStateManager>
    )
}
