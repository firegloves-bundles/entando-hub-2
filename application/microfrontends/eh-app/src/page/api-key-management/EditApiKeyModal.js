import { Button, ComposedModal, ModalBody, ModalFooter, ModalHeader, TextInput } from 'carbon-components-react';
import { useEffect, useState } from 'react';

import i18n from '../../i18n';

const EditApiKeyModal = ({ open, apiKeyData, onClose, onSubmit }) => {
  const [apiKeyName, setApiKeyName] = useState('');

  useEffect(() => {
    setApiKeyName(apiKeyData?.label || '');
  }, [apiKeyData]);

  const handleApiKeyNameChange = (e) => {
    setApiKeyName(e.target.value);
  };

  const handleSubmit = () => {
    onSubmit({
      id: apiKeyData.id,
      label: apiKeyName,
    });
  };

  return (
    <ComposedModal
      className="EditApiKeyModal"
      open={open}
      onClose={onClose}
      size="sm"
      preventCloseOnClickOutside
    >
      <ModalHeader
        label={i18n.t('component.button.editApiKey')}
        buttonOnClick={onClose}
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
          onClick={onClose}
        >
          {i18n.t('component.button.cancel')}
        </Button>
        <Button
          kind="primary"
          onClick={handleSubmit}
        >
          {i18n.t('component.button.save')}
        </Button>
      </ModalFooter>
    </ComposedModal>
  );
};

export default EditApiKeyModal;
