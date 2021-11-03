import { OverflowMenu, OverflowMenuItem } from "carbon-components-react"
import { useState } from "react"
import { ModalUpdateCategory } from "../modal-update-category/ModalUpdateCategory"
import {
  getSingleCategory,
} from "../../../integration/Integration"

const CategoryManagementOverflowMenu = ({
  categoryObj,
  onAfterSubmit,
  setReloadToken,
}) => {
  const [openModal, setOpenModal] = useState(false)

  return (
    <>
      <OverflowMenu>
        <OverflowMenuItem itemText="Edit" onClick={() => setOpenModal(true)} />
      </OverflowMenu>
      {openModal && (
        <ModalUpdateCategory
          categoryObj={categoryObj}
          open={openModal}
          onCloseModal={() => setOpenModal(false)}
          onAfterSubmit={onAfterSubmit}
        />
      )}
    </>
  )
}

export default CategoryManagementOverflowMenu
