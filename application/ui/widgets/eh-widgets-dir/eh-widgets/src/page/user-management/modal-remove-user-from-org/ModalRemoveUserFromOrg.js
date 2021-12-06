import { Modal, Loading } from "carbon-components-react"
import { useState, useEffect } from "react";
import { removeUserFromOrganisation } from "../../../integration/Integration";
import "./modal-remove-user-from-org.scss";
import RemoveUserFromOrg from "./remove-user-from-org/RemoveUserFromOrg";
import { BUTTON_LABELS } from "../../../helpers/constants";

export const ModalRemoveUserFromOrg = ({userObj, open, onCloseModal, onAfterSubmit}) => {

    const [user] = useState(userObj)
    const [visible, setVisible] = useState(false)

    useEffect(() => {
        (async () => {
            setVisible(true)
        })()
    }, [userObj])


    const onRequestClose = (e) => {
        onCloseModal()
    }

    const removeUser = async (user) => {
        await removeUserFromOrganisation(user && user.organisation && user.organisation.organisationId, user && user.username);
    }

    const onRequestSubmit = (e) => {
        (async () => {
            await removeUser(user)
            onCloseModal()
            onAfterSubmit()
        })()
    }

    return (
        <>
            {!visible && <Loading />}
            <Modal
                style={{ display: !visible ? "none" : "" }}
                className="ModalRemoveUserFromOrg"
                modalLabel={BUTTON_LABELS.REMOVE}
                primaryButtonText={BUTTON_LABELS.REMOVE}
                secondaryButtonText={BUTTON_LABELS.CANCEL}
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestSubmit}>
                <RemoveUserFromOrg />
            </Modal>
        </>
    )
}
