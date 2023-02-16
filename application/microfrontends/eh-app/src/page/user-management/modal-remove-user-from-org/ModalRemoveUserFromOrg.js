import { Modal, Loading } from "carbon-components-react"
import { useState, useEffect } from "react";
import { removeUserFromOrganisation } from "../../../integration/Integration";
import "./modal-remove-user-from-org.scss";
import RemoveUserFromOrg from "./remove-user-from-org/RemoveUserFromOrg";
import i18n from "../../../i18n";
import { useApiUrl } from "../../../contexts/ConfigContext";

export const ModalRemoveUserFromOrg = ({ userObj, open, onCloseModal, onAfterSubmit }) => {

    const [user] = useState(userObj)
    const [visible, setVisible] = useState(false)

    const apiUrl = useApiUrl();

    useEffect(() => {
        (async () => {
            setVisible(true)
        })()
    }, [userObj])


    const onRequestClose = (e) => {
        onCloseModal()
    }

    const removeUser = async (user) => {
        await removeUserFromOrganisation(apiUrl, user && user.organisation && user.organisation.organisationId, user && user.username, 'delete');
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
                modalLabel={i18n.t('component.button.remove')}
                primaryButtonText={i18n.t('component.button.remove')}
                secondaryButtonText={i18n.t('component.button.cancel')}
                open={open}
                onRequestClose={onRequestClose}
                onRequestSubmit={onRequestSubmit}>
                <RemoveUserFromOrg />
            </Modal>
        </>
    )
}
