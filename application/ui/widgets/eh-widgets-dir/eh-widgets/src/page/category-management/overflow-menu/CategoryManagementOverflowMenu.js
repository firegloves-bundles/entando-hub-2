import { OverflowMenu, OverflowMenuItem } from "carbon-components-react"
import { useState } from "react"
import { ModalUpdateCategory } from "../modal-update-category/ModalUpdateCategory"
import { ModalDeleteCategory } from "../modal-delete-category/ModalDeleteCategory"

const CategoryManagementOverflowMenu = ({
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
        <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)} />
        <OverflowMenuItem itemText="Delete" onClick={() => setOpenDeleteModal(true)} />
      </OverflowMenu>
      {openModal && (
        <ModalUpdateCategory
          categoryObj={categoryObj}
          open={openModal}
          onCloseModal={() => setOpenModal(false)}
          onAfterSubmit={onAfterSubmit}
        />
      )}
      {openDeleteModal && (
        <ModalDeleteCategory
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
