import {Button, Modal} from "carbon-components-react";
import {Add16} from '@carbon/icons-react';
import ReactDOM from "react-dom";
import {useState} from "react";


/*
    TODO code cut and pasted from the crabon documentation can be semplified
    This component manages the modal for adding a new bundle group
*/
export const ModalAddNewUser = ({onAfterSubmit}) => {


    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false);
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data


        const onDataChange = (newUser)=>{
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
                resetData()
                onAfterSubmit()

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
        );
    };





    return (
        <ModalStateManager
            renderLauncher={({onRequestOpen}) => (
                <Button onClick={onRequestOpen} renderIcon={Add16}>Add User</Button>
            )}>
            {({open, onRequestClose, onDataChange, onRequestSubmit, elemKey}) => (
                <Modal
                    modalLabel="Add"
                    primaryButtonText="Add"
                    secondaryButtonText="Cancel"
                    open={open}
                    onRequestClose={onRequestClose}
                    onRequestSubmit={onRequestSubmit}>

                </Modal>
            )}
        </ModalStateManager>
    );
};
