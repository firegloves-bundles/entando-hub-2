import { Modal } from "carbon-components-react"
import { useCallback, useState } from "react"
import UpdateOrganisation from "./update-organisation/UpdateOrganisation"
import {editOrganisation, getSingleOrganisation} from "../../../integration/Integration"
import { organisationSchema } from "../../../helpers/validation/organisationSchema"
import { fillErrors } from "../../../helpers/validation/fillErrors"
import "./modal-update-organization.scss"
import i18n from "../../../i18n"

export const ModalUpdateOrganisation = ({
  apiUrl,
  organisationObj,
  open,
  onCloseModal,
  onAfterSubmit,
}) => {
  const [organisation, setOrganisation] = useState(organisationObj)
  const [validationResult, setValidationResult] = useState({})

  const onDataChange = useCallback((newOrganisationObj) => {
    setOrganisation(newOrganisationObj)
  }, [])

  const onRequestClose = (e) => {
    onCloseModal()
  }

  const getBundleGroupsForAnOrganisation = async (organisationId) => {
        const org = await getSingleOrganisation(apiUrl, organisationId)
        return org.organisation.bundleGroups
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
            const bundleGroups = await getBundleGroupsForAnOrganisation(organisation.organisationId)
            await editOrganisation(apiUrl, {
                name: organisation.name,
                description: organisation.description,
                bundleGroups: bundleGroups
            }, organisation.organisationId)
            onCloseModal()
            onAfterSubmit()
        })()
  }

  return (
        <Modal
            modalLabel={i18n.t('component.button.edit')}
            className="Modal-Update-organization"
            primaryButtonText={i18n.t('component.button.save')}
            secondaryButtonText={i18n.t('component.button.cancel')}
            open={open}
            onRequestClose={onRequestClose}
            onRequestSubmit={onRequestSubmit}>
            <UpdateOrganisation
               organisationObj={organisation}
               onDataChange={onDataChange}
               validationResult={validationResult}/>
        </Modal>
  )
}
