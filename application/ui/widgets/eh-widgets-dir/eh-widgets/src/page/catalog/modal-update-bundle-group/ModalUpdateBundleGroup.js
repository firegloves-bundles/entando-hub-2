import {Button, Modal} from "carbon-components-react";
import {Add16} from '@carbon/icons-react';
import ReactDOM from "react-dom";
import {useState} from "react";
import UpdateBundleGroup from "./update-boundle-group/UpdateBundleGroup";
import {addNewBundle, addNewBundleGroup} from "../../../integration/Integration";


export const ModalUpdateBundleGroup = ({afterSubmit}) => {


    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false);
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data
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

        const onRequestSubmit = (e) => {
            e.preventDefault();
            (async () => {
                //create bundle children
                let newChildren = []
                if(newBundleGroup.children && newBundleGroup.children.length) {
                    let respArray = await Promise.all(newBundleGroup.children.map(addNewBundle))
                    console.log("respArray", respArray)
                    newChildren = respArray.map(res => res.newBundle.data.bundleId)
                }
                console.log("newChildren", newChildren)
                const toSend = {
                    ...newBundleGroup,
                    children: newChildren
                }
                const res = await addNewBundleGroup(toSend)
                console.log("addNewBundleGroup", res)
                setNewBundleGroup(toSend)
                resetData()
                afterSubmit()

            })()
        };

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
        );
    };





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
                    <UpdateBundleGroup key={elemKey} onDataChange={onDataChange}/>
                </Modal>
            )}
        </ModalStateManager>
    );
};
