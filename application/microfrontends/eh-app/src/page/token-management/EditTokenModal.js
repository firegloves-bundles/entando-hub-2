import { Button, ComposedModal, ModalBody, ModalFooter, ModalHeader, TextInput } from 'carbon-components-react';
import { useEffect, useState } from 'react';

import i18n from '../../i18n';

const EditTokenModal = ({ open, tokenData, onClose, onSubmit }) => {
  const [tokenName, setTokenName] = useState('');

  useEffect(() => {
    setTokenName(tokenData?.name || '');
  }, [tokenData]);

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
      preventCloseOnClickOutside
    >
      <ModalHeader
        label={i18n.t('component.button.editToken')}
        buttonOnClick={onClose}
      />
      <ModalBody>
        <TextInput
          id="name"
          labelText={`${i18n.t('component.bundleModalFields.name')}`}
          value={tokenName}
          onChange={handleTokenNameChange}
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

export default EditTokenModal;
