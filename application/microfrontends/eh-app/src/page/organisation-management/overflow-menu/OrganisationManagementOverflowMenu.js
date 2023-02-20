import { OverflowMenu, OverflowMenuItem } from "carbon-components-react"
import { useState } from "react"
import { ModalUpdateOrganisation } from "../modal-update-organisation/ModalUpdateOrganisation"
import {
  getSingleOrganisation,
  deleteOrganisation,
} from "../../../integration/Integration"
import i18n from "../../../i18n"
import { useApiUrl } from "../../../contexts/ConfigContext"

const OrganisationManagementOverflowMenu = ({
  organisationObj,
  onAfterSubmit,
  setReloadToken,
  onCreatePrivateCatalog,
  onNavigatePrivateCatalog,
}) => {
  const [openModal, setOpenModal] = useState(false)

  const apiUrl = useApiUrl();

  const deleteHandler = async () => {
    const org = await getSingleOrganisation(apiUrl,organisationObj.organisationId)

    await deleteOrganisation(apiUrl,org.organisation.organisationId)

    setReloadToken(new Date().getTime().toString())
  }

  return (
    <>
      <OverflowMenu>
        <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)} />
        {organisationObj.privateCatalog ? (
          <OverflowMenuItem itemText={i18n.t('component.button.navigatePrivateCatalog')} onClick={onNavigatePrivateCatalog} />
        ) : (
          <OverflowMenuItem itemText={i18n.t('component.button.createPrivateCatalog')} onClick={onCreatePrivateCatalog} />
        )}
        <OverflowMenuItem itemText={i18n.t('component.button.delete')} onClick={deleteHandler} />
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
