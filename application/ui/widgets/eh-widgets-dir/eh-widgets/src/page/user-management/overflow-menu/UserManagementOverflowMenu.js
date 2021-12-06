import { OverflowMenu, OverflowMenuItem } from "carbon-components-react";
import { useState } from "react";
import { ModalUpdateUser } from "../modal-update-user/ModalUpdateUser";
import { ModalRemoveUserFromOrg } from "../modal-remove-user-from-org/ModalRemoveUserFromOrg";
import { DROPDOWN_OPTIONS } from "../../../helpers/constants";

const UserManagementOverflowMenu = ({ userObj, onAfterSubmit }) => {
    const [openModal, setOpenModal] = useState(false)
    const [openDeleteModal, setOpenDeleteModal] = useState(false)
    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText={DROPDOWN_OPTIONS.EDIT} onClick={() => setOpenModal(true)} />
                <OverflowMenuItem itemText={DROPDOWN_OPTIONS.REMOVE} onClick={() => setOpenDeleteModal(true)} />
            </OverflowMenu>

            {openModal && <ModalUpdateUser userObj={userObj} open={openModal}
                onCloseModal={() => setOpenModal(false)} onAfterSubmit={onAfterSubmit} />}

            {openDeleteModal && <ModalRemoveUserFromOrg userObj={userObj} open={openDeleteModal}
                onCloseModal={() => setOpenDeleteModal(false)} onAfterSubmit={onAfterSubmit} />}
        </>
    )
}

export default UserManagementOverflowMenu
