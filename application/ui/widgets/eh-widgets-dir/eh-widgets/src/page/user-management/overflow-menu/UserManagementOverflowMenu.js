import {OverflowMenu, OverflowMenuItem} from "carbon-components-react";
import {useState} from "react";
import {ModalUpdateUser} from "../modal-update-user/ModalUpdateUser";

const UserManagementOverflowMenu = (userName, onAfterSubmit) => {
    const [openModal, setOpenModal] = useState(false)
    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)}/>
            </OverflowMenu>
            {openModal && <ModalUpdateUser open={openModal}
                                                  onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}

        </>
    )
}

export default UserManagementOverflowMenu
