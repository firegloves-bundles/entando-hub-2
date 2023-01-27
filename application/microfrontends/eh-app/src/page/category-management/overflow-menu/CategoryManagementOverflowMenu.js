import { OverflowMenu, OverflowMenuItem } from "carbon-components-react"
import { useState } from "react"
import { ModalUpdateCategory } from "../modal-update-category/ModalUpdateCategory"
import { ModalDeleteCategory } from "../modal-delete-category/ModalDeleteCategory"
import i18n from "../../../i18n"

const CategoryManagementOverflowMenu = ({
  apiUrl,
  categoryObj,
  onAfterSubmit,
  setReloadToken,
  categories,
}) => {
  const [openModal, setOpenModal] = useState(false)
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  return (
    <>
      <OverflowMenu>
        <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)} />
        <OverflowMenuItem itemText={i18n.t('component.button.delete')} onClick={() => setOpenDeleteModal(true)} />
      </OverflowMenu>
      {openModal && (
        <ModalUpdateCategory
          apiUrl={apiUrl}
          categoryObj={categoryObj}
          open={openModal}
          onCloseModal={() => setOpenModal(false)}
          onAfterSubmit={onAfterSubmit}
        />
      )}
      {openDeleteModal && (
        <ModalDeleteCategory
          apiUrl={apiUrl}
          categoryObj={categoryObj}
          open={openDeleteModal}
          onCloseModal={() => setOpenDeleteModal(false)}
          onAfterSubmit={onAfterSubmit}
          categories={categories}
        />
      )}
    </>
  )
}

export default CategoryManagementOverflowMenu
