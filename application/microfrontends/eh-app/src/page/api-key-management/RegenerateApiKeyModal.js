import { Button, ComposedModal, ModalBody, ModalFooter, ModalHeader } from 'carbon-components-react';

import i18n from '../../i18n';

const RegenerateApiKeyModal = ({ open, apiKeyData, onClose, onConfirm }) => {
  return (
    <ComposedModal
      className="RegenerateApiKeyModal"
      open={open}
      onClose={onClose}
      size="xs"
    >
      <ModalHeader
        label={`${i18n.t('modalMsg.regenerateApiKeyTitle')} ${apiKeyData?.label || ''}`}
        buttonOnClick={onClose}
      />
      <ModalBody>
        {i18n.t('modalMsg.regenerateApiKey')}
      </ModalBody>
      <ModalFooter>
        <Button
          kind="secondary"
          onClick={onClose}
        >
          {i18n.t('component.button.cancel')}
        </Button>
        <Button
          kind="primary"
          onClick={onConfirm}
        >
          {i18n.t('component.button.confirm')}
        </Button>
      </ModalFooter>
    </ComposedModal>
  );
};

export default RegenerateApiKeyModal;
