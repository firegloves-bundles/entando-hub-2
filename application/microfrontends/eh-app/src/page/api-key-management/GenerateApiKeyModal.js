import { Button, ComposedModal, ModalBody, ModalFooter, ModalHeader, TextInput } from 'carbon-components-react';
import { useState } from 'react';

import i18n from '../../i18n';

const GenerateApiKeyModal = ({ open, onClose, onSubmit }) => {
  const [apiKeyName, setApiKeyName] = useState('');

  const handleApiKeyNameChange = (e) => {
    setApiKeyName(e.target.value);
  };

  const handleClose = () => {
    setApiKeyName('');
    onClose();
  };

  const handleSubmit = () => {
    onSubmit({ label: apiKeyName });
  };

  return (
    <ComposedModal
      className="GenerateApiKeyModal"
      open={open}
      onClose={onClose}
      size="sm"
      preventCloseOnClickOutside
    >
      <ModalHeader
        label={i18n.t('component.button.generateApiKey')}
        buttonOnClick={handleClose}
      />
      <ModalBody>
        <TextInput
          id="name"
          labelText={`${i18n.t('component.bundleModalFields.name')}`}
          value={apiKeyName}
          onChange={handleApiKeyNameChange}
          maxLength={128}
        />
      </ModalBody>
      <ModalFooter>
        <Button
          kind="secondary"
          onClick={handleClose}
        >
          {i18n.t('component.button.cancel')}
        </Button>
        <Button
          kind="primary"
          onClick={handleSubmit}
        >
          {i18n.t('component.button.generateApiKey')}
        </Button>
      </ModalFooter>
    </ComposedModal>
  );
};

export default GenerateApiKeyModal;
