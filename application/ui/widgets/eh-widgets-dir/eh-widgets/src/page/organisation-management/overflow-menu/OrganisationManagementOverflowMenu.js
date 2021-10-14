import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import {useState} from "react"
import {ModalUpdateOrganisation} from "../modal-update-organisation/ModalUpdateOrganisation"

const OrganisationManagementOverflowMenu = ({organisationObj, onAfterSubmit}) => {
    const [openModal, setOpenModal] = useState(false)
    return (
        <>
            <OverflowMenu>
                <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)}/>
            </OverflowMenu>
            {openModal && <ModalUpdateOrganisation organisationObj={organisationObj} open={openModal}
                                                  onCloseModal={() => setOpenModal(false)}  onAfterSubmit={onAfterSubmit}/>}

        </>
    )
}

export default OrganisationManagementOverflowMenu
