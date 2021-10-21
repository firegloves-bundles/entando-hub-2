import {Modal} from "carbon-components-react"
import {useCallback, useState} from "react"
import UpdateOrganisation from "./update-organisation/UpdateOrganisation"
import {editOrganisation} from "../../../integration/Integration"

import "./modal-update-organization.scss"

export const ModalUpdateOrganisation = ({organisationObj, open, onCloseModal, onAfterSubmit}) => {

    const [organisation, setOrganisation] = useState(organisationObj)

    const onDataChange = useCallback((newOrganisationObj) => {
        setOrganisation(newOrganisationObj)
    }, [])

    const onRequestClose = (e) => {
        onCloseModal()
    }


    const onRequestSubmit = (e) => {
        (async () => {
            await editOrganisation({
                name: organisation.name,
                description: organisation.description
            }, organisation.organisationId)
            onCloseModal()
            onAfterSubmit()

        })()
    }

    return (
        <Modal
            modalLabel="Edit"
            className="Modal-Update-organization"
            primaryButtonText="Save"
            secondaryButtonText="Cancel"
            open={open}
            onRequestClose={onRequestClose}
            onRequestSubmit={onRequestSubmit}>
            <UpdateOrganisation organisationObj={organisation} onDataChange={onDataChange}/>
        </Modal>
    )
}
