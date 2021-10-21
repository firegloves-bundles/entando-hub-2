import { OverflowMenu, OverflowMenuItem } from "carbon-components-react"
import { useState } from "react"
import { ModalUpdateOrganisation } from "../modal-update-organisation/ModalUpdateOrganisation"
import {
  getSingleOrganisation,
  deleteOrganisation,
} from "../../../integration/Integration"

const OrganisationManagementOverflowMenu = ({
  organisationObj,
  onAfterSubmit,
  setReloadToken,
}) => {
  const [openModal, setOpenModal] = useState(false)

  const deleteHandler = async () => {
    const org = await getSingleOrganisation(organisationObj.organisationId)

    if (
      org.organisation.bundleGroups.length > 0 &&
      org.organisation.bundleGroups.length < 2
    ) {
      console.log(
        `Impossible to delete ${org.organisation.name}: there is ${org.organisation.bundleGroups.length} bundle group linked to it.`
      )
    } else if (org.organisation.bundleGroups.length > 1) {
      console.log(
        `Impossible to delete ${org.organisation.name}: there are ${org.organisation.bundleGroups.length} bundle groups linked to it.`
      )
    } else {
      const delResponse = await deleteOrganisation(
        org.organisation.organisationId
      )
      console.log(`${delResponse} deleted`)
    }

    const customEvent = new CustomEvent("delete-organisation", {
      detail: { message: `Organisation created!` },
    })
    window.dispatchEvent(customEvent)
    setReloadToken(new Date().getTime().toString())
  }

  return (
    <>
      <OverflowMenu>
        <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)} />
        <OverflowMenuItem itemText="Delete" onClick={deleteHandler} />
      </OverflowMenu>
      {openModal && (
        <ModalUpdateOrganisation
          organisationObj={organisationObj}
          open={openModal}
          onCloseModal={() => setOpenModal(false)}
          onAfterSubmit={onAfterSubmit}
        />
      )}
    </>
  )
}

export default OrganisationManagementOverflowMenu
