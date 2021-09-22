import {Modal} from "carbon-components-react";
import {useCallback, useState} from "react";


export const ModalUpdateUser = ({userName, open, onCloseModal, onAfterSubmit}) => {

    const onDataChange = useCallback((user) => {
    }, [])

    const onRequestClose = (e) => {
        onCloseModal()
    }


    const onRequestSubmit = (e) => {
        onCloseModal()
        onAfterSubmit()

    }

    return (
        <Modal
            modalLabel="Edit"
            primaryButtonText="Save"
            secondaryButtonText="Cancel"
            open={open}
            onRequestClose={onRequestClose}
            onRequestSubmit={onRequestSubmit}>
        </Modal>
    )
}
