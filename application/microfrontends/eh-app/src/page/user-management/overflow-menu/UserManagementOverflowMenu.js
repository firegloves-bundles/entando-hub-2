import { OverflowMenu, OverflowMenuItem } from "carbon-components-react";
import { useState } from "react";
import { ModalUpdateUser } from "../modal-update-user/ModalUpdateUser";
import { ModalRemoveUserFromOrg } from "../modal-remove-user-from-org/ModalRemoveUserFromOrg";
import i18n from "../../../i18n";

const UserManagementOverflowMenu = ({ apiUrl, userObj, onAfterSubmit }) => {
    const [openModal, setOpenModal] = useState(false)
    const [openDeleteModal, setOpenDeleteModal] = useState(false)
    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)} />
                <OverflowMenuItem itemText={i18n.t('component.button.remove')} onClick={() => setOpenDeleteModal(true)} />
            </OverflowMenu>

            {openModal && <ModalUpdateUser apiUrl={apiUrl} userObj={userObj} open={openModal}
                onCloseModal={() => setOpenModal(false)} onAfterSubmit={onAfterSubmit} />}

            {openDeleteModal && <ModalRemoveUserFromOrg apiUrl={apiUrl} userObj={userObj} open={openDeleteModal}
                onCloseModal={() => setOpenDeleteModal(false)} onAfterSubmit={onAfterSubmit} />}
        </>
    )
}

export default UserManagementOverflowMenu
