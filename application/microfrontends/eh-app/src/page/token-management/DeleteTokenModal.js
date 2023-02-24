import { Button, ComposedModal, ModalBody, ModalFooter, ModalHeader } from 'carbon-components-react';

import i18n from '../../i18n';

const DeleteTokenModal = ({ open, onClose, onConfirm }) => {
  return (
    <ComposedModal
      className="DeleteTokenModal"
      open={open}
      onClose={onClose}
      size="xs"
    >
      <ModalHeader label={i18n.t('component.button.deleteToken')} />
      <ModalBody>
        {i18n.t('modalMsg.deleteToken')}
      </ModalBody>
      <ModalFooter>
        <Button
          kind="secondary"
          onClick={onClose}
        >
          {i18n.t('component.button.cancel')}
        </Button>
        <Button
          kind="danger"
          onClick={onConfirm}
        >
          {i18n.t('component.button.delete')}
        </Button>
      </ModalFooter>
    </ComposedModal>
  );
};

export default DeleteTokenModal;
