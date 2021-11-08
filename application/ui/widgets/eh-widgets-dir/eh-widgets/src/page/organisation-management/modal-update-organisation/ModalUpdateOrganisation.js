import {Modal} from "carbon-components-react"
import {useCallback, useState} from "react"
import UpdateOrganisation from "./update-organisation/UpdateOrganisation"
import {editOrganisation} from "../../../integration/Integration"
import { organisationSchema } from "../../../helpers/validation/organisationSchema"
import { fillErrors } from "../../../helpers/validation/fillErrors"
import "./modal-update-organization.scss"

export const ModalUpdateOrganisation = ({organisationObj, open, onCloseModal, onAfterSubmit}) => {

    const [organisation, setOrganisation] = useState(organisationObj)
    const [validationResult, setValidationResult] = useState({})

    const onDataChange = useCallback((newOrganisationObj) => {
        setOrganisation(newOrganisationObj)
    }, [])

    const onRequestClose = (e) => {
        onCloseModal()
    }


    const onRequestSubmit = (e) => {
        (async () => {
            let validationError
            await organisationSchema.validate(organisation, {abortEarly: false}).catch(error => {
                validationError = fillErrors(error)
            })
            if (validationError) {
                setValidationResult(validationError)
                return //don't send the form
            }
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
            <UpdateOrganisation organisationObj={organisation} onDataChange={onDataChange} validationResult={validationResult}/>
        </Modal>
    )
}
