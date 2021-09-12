import {Button, Modal} from "carbon-components-react";
import {Add16} from '@carbon/icons-react';
import ReactDOM from "react-dom";
import {useState} from "react";
import NewBundleGroup from "../../bundle-group/new-boundle-group/NewBundleGroup";
import onRequestSubmit from "carbon-components-react/lib/components/Modal/Modal";
import {addNewBundle, addNewBundleGroup} from "../../../integration/Integration";


export const ModalAddNewBundleGroup = () => {


    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false);
        const [newBundleGroup, setNewBundleGroup] = useState({})

        const onDataChange = (newBundleGroup)=>{
            console.log("onDataChange",newBundleGroup)
            setNewBundleGroup(newBundleGroup)
        }

        const onRequestSubmit = (e) => {
            e.preventDefault();
            (async () => {
                //create bundle children
                let respArray = await Promise.all(newBundleGroup.children.map(addNewBundle))
                console.log("respArray", respArray)
                const newChildren = respArray.map(res => res.newBundle.data.bundleId)
                console.log("newChildren", newChildren)
                const toSend = {
                    ...newBundleGroup,
                    children: newChildren
                }
                setNewBundleGroup(toSend)
                const res = addNewBundleGroup(toSend)
                await console.log(res)

            })()
        };

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent open={open} setOpen={setOpen} onDataChange={onDataChange} onRequestSubmit={onRequestSubmit}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent open={open} setOpen={setOpen}/>}
            </>
        );
    };





    return (
        <ModalStateManager
            renderLauncher={({setOpen}) => (
                <Button onClick={() => setOpen(true)} renderIcon={Add16}>Add</Button>
            )}>
            {({open, setOpen, onDataChange, onRequestSubmit}) => (
                <Modal
                    modalLabel="Add"
                    primaryButtonText="Add"
                    secondaryButtonText="Cancel"
                    open={open}
                    onRequestClose={() => setOpen(false)}
                    onRequestSubmit={onRequestSubmit}
                >
                    <NewBundleGroup onDataChange={onDataChange}/>
                </Modal>
            )}
        </ModalStateManager>
    );
};
