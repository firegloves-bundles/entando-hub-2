import {Modal} from "carbon-components-react"
import {useCallback, useState} from "react"
import UpdateOrganisation from "./update-organisation/UpdateOrganisation"
import {editOrganisation} from "../../../integration/Integration"
import { fireEvent, SUCCESS, FAIL } from "../../../helpers/eventDispatcher"

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
            const org = await editOrganisation({
                name: organisation.name,
                description: organisation.description
            }, organisation.organisationId)
            console.log("updated org", org)
            if (org.isError) {
                fireEvent(FAIL, `Impossible to update organisation: ${org.errorBody.message}`)
            } else {
                fireEvent(SUCCESS, `Organisation ${org.editedOrganisation.data.name} updated`)
            }
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
