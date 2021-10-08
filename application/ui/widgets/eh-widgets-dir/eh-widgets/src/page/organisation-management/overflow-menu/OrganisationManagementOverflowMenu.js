import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import {useState} from "react"
import {ModalUpdateOrganisation} from "../modal-update-organisation/ModalUpdateOrganisation"

const OrganisationManagementOverflowMenu = ({userObj, onAfterSubmit}) => {
    const [openModal, setOpenModal] = useState(false)
    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)}/>
            </OverflowMenu>
            {openModal && <ModalUpdateOrganisation userObj={userObj} open={openModal}
                                                  onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}

        </>
    )
}

export default OrganisationManagementOverflowMenu
