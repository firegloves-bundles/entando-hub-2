import { Button, ComposedModal, ModalBody, ModalFooter, ModalHeader, TextInput } from 'carbon-components-react';
import { useState } from 'react';

import i18n from '../../i18n';

const EditTokenModal = ({ open, onClose, onSubmit }) => {
  const [tokenName, setTokenName] = useState('');

  const handleTokenNameChange = (e) => {
    setTokenName(e.target.value);
  };

  const handleSubmit = () => {
    onSubmit({ tokenName });
  };

  return (
    <ComposedModal
      className="EditTokenModal"
      open={open}
      onClose={onClose}
      size="xs"
    >
      <ModalHeader label={i18n.t('component.button.editToken')} />
      <ModalBody>
        <TextInput
          id="name"
          labelText={`${i18n.t('component.bundleModalFields.name')}`}
          value={tokenName}
          onChange={handleTokenNameChange}
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

export default EditTokenModal;
