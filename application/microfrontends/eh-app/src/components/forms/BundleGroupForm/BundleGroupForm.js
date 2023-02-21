import {
    Checkbox,
    Column,
    Content,
    Grid,
    Row,
    Select,
    SelectItem,
    TextArea,
    TextInput
} from "carbon-components-react";
import { useEffect, useRef, useState } from "react";
import values from "../../../config/common-configuration";
import {
    BUNDLE_STATUS,
    CHAR_LENGTH,
    CHAR_LENGTH_255,
    CHAR_LIMIT_MSG_SHOW_TIME,
    DESCRIPTION_FIELD_ID,
    DOCUMENTATION_ADDRESS_URL_REGEX,
    DOCUMENTATION_FIELD_ID,
    MAX_CHAR_LENGTH,
    MAX_CHAR_LENGTH_FOR_DESC,
    NAME_FIELD_ID,
    VERSION_FIELD_ID,
    VERSION_REGEX,
    CONTACT_URL_REGEX,
    CONTACT_URL_FIELD_ID
} from "../../../helpers/constants";
import { bundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import i18n from "../../../i18n";
import './bundle-group-form.scss';
import BundlesOfBundleGroup from "./update-bundle-group/bundles-of-bundle-group/BundlesOfBundleGroup";
import IconUploader from "./update-bundle-group/icon-uploader/IconUploader";
import "./update-bundle-group/update-bundle-group.scss";
import { isHubAdmin } from "../../../helpers/helpers";


const BundleGroupForm = ({
                             bundleGroup,
                             categories,
                             allowedOrganisations, //organisations on which the user can operate
                             onDataChange,
                             selectStatusValues,
                             validationResult,
                             minOneBundleError,
                             theBundleStatus,
                             mode,
                             orgList
                         }) => {

    const [bundleStatus, setBundleStatus] = useState(theBundleStatus ? theBundleStatus : mode === 'Add' ? BUNDLE_STATUS.NOT_PUBLISHED : "");
    const [bundleNameLength, setBundleNameLength] = useState(0);
    const [bundleDescriptionLength, setBundleDescriptionLength] = useState(0);
    const [isDocumentationAddressValid, setIsDocumentationAddressValid] = useState(false);
    const [isBundleVersionValid, setIsBundleVersionValid] = useState(false);
    const [isContactUrlValid, setIsContactUrlValid] = useState(false);

    const [showNameCharLimitErrMsg, setShowNameCharLimitErrMsg] = useState(false);
    const [showDescriptionCharLimitErrMsg, setShowDescriptionCharLimitErrMsg] = useState(false);
    const [showDocUrlCharLimitErrMsg, setShowDocUrlCharLimitErrMsg] = useState(false);
    const [showVersionCharLimitErrMsg, setShowVersionCharLimitErrMsg] = useState(false);
    const [showContactUrlCharLimitErrMsg, setShowContactUrlCharLimitErrMsg] = useState(false);

    const [mounted, setMounted] = useState(false);
    const timerRef = useRef(null);

    const renderOrganisationColumn = (currOrganisationId, organisations) => {
        if(!currOrganisationId) return;

        const currOrganisation = organisations.find(o => Number(o.organisationId) === Number(currOrganisationId))
        const enableOrg = isHubAdmin() && bundleGroup.isEditable &&
            (bundleGroup.versionDetails.status === BUNDLE_STATUS.NOT_PUBLISHED || bundleGroup.versionDetails.status === BUNDLE_STATUS.PUBLISH_REQ)
        let organisationSelectItems = organisations.length && organisations.map((o) => {
            return (
                <SelectItem
                    key={o.organisationId}
                    value={o.organisationId}
                    text={o.name}
                />
            )
        })

        let selectItem = <Select
            disabled={disabled}
            value={currOrganisation && currOrganisation.organisationId}
            onChange={organisationChangeHandler}
            id={"organisation"}
            labelText={i18n.t('component.bundleModalFields.organisation')}>
            {organisationSelectItems}
        </Select>

        if (mode === 'Add' && orgList.length && isHubAdmin()) {
            return (<Column sm={16} md={16} lg={16}>
                {selectItem}
            </Column>)
        }

        if (!enableOrg && currOrganisation) {
            return (<Column sm={16} md={16} lg={16}>
                <TextInput
                    disabled={true}
                    id="organisation"
                    labelText={i18n.t('component.bundleModalFields.organisation')}
                    value={currOrganisation.name ? currOrganisation.name : currOrganisation.organisationName}
                />
            </Column>)
        }

        if (enableOrg && orgList.length) {
            return (<Column sm={16} md={16} lg={16}>{selectItem}</Column>)
        }

    }

    const changeBundleGroup = (field, value) => {
        const newObj = {
            ...bundleGroup,
        }
        newObj[field] = value
        onDataChange(newObj)
    }

    const createVersionDetailsObj = (field, value) => {
        const versionDetails = {
            ...bundleGroup.versionDetails,
        }
        versionDetails[field] = value
        changeBundleGroup("versionDetails", versionDetails)
    }

    const disabled = selectStatusValues.disabled
    const createSelectOptionsForRoleAndSetSelectStatus =
        selectStatusValues.values.map((curr, index) => (
            <SelectItem key={index} value={curr.value} text={i18n.t(curr.text)}/>
        ))

    const selectItems_Category = categories && categories.map((category) => {
        return (
            <SelectItem
                key={category.categoryId}
                value={category.categoryId}
                text={category.name}
            />
        )
    })

    const nameChangeHandler = (e) => {
        setBundleNameLength(e.target.value.trim().length);
        if (e.target.value.trim().length < CHAR_LENGTH) {
            const errorMessageForLengthZeroOrThree = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
            validationResult["name"] = [errorMessageForLengthZeroOrThree]
        } else if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
            validationResult["name"] = [i18n.t('formValidationMsg.max25Char')]
        }
        changeBundleGroup("name", e.target.value)
    }

    /**
     * @param {*} e Event object to get value of field
     * @param {*} field Name of the field
     * @description Trimming whitespaces from the field value.
     */
    const trimBeforeFormSubmitsHandler = (e, field) => {
        changeBundleGroup(field, e.target.value.trim())
    }

    const organisationChangeHandler = (e) => {
        const selectedOrganisationId = e.target.value
        changeBundleGroup("organisationId", selectedOrganisationId)
        changeBundleGroup("catalogId", orgList.find(org => org.organisationId === selectedOrganisationId)?.catalogId)
    }

    const categoryChangeHandler = (e) => {
        changeBundleGroup("categories", [e.target.value])
    }

    const contactUrlChangeHandler = (e) => {
        createVersionDetailsObj("contactUrl", e.target.value);
        setIsValid(e.target.value.trim(), 'contactUrl')
        if (!e.target.value.trim().length) {
            validationResult["versionDetails.contactUrl"] = [i18n.t('formValidationMsg.contactUrlRequired')]
        }
        else {
            validationResult["versionDetails.contactUrl"] = [i18n.t('formValidationMsg.contactUrlFormat')]
        }
    }

    const displayContactChangeHandler = (value, name, e) => {
        createVersionDetailsObj("displayContactUrl", value);
    }

    const documentationChangeHandler = (e) => {
        createVersionDetailsObj("documentationUrl", e.target.value);
        setIsValid(e.target.value.trim(), 'documentationUrl')
        if (!e.target.value.trim().length) {
            validationResult["versionDetails.documentationUrl"] = [i18n.t('formValidationMsg.docRequired')]
        } else {
            validationResult["versionDetails.documentationUrl"] = [i18n.t('formValidationMsg.docFormat')]
        }
    }

    const versionChangeHandler = (e) => {
        createVersionDetailsObj("version", e.target.value);
        if (!e.target.value.trim().length) {
            validationResult["versionDetails.version"] = [i18n.t('formValidationMsg.versionRequired')]
            setIsBundleVersionValid(false);
        } else if (!(e.target.value.trim().length > 0 && new RegExp(VERSION_REGEX).test(e.target.value))) {
            validationResult["versionDetails.version"] = [i18n.t('formValidationMsg.versionFormat')]
            setIsBundleVersionValid(false);
        } else {
            setIsBundleVersionValid(true);
        }
    }

    const setIsValid = (val, inputTypeName) => {
        if (inputTypeName === 'documentationUrl') {
            val.trim().length > 0 && new RegExp(DOCUMENTATION_ADDRESS_URL_REGEX).test(val) ? setIsDocumentationAddressValid(true) : setIsDocumentationAddressValid(false)
        } else if (inputTypeName === 'version') {
            val.trim().length > 0 && new RegExp(VERSION_REGEX).test(val) ? setIsBundleVersionValid(true) : setIsBundleVersionValid(false);
        } else if (inputTypeName === 'contactUrl') {
            val.trim().length > 0 && new RegExp(CONTACT_URL_REGEX).test(val) ? setIsContactUrlValid(true) : setIsContactUrlValid(false)
        }
    }

    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const fileReader = new FileReader()
            fileReader.readAsDataURL(file)
            fileReader.onload = () => {
                resolve(fileReader.result)
            }
            fileReader.onerror = (error) => {
                reject(error)
            }
        })
    }

    const imagesChangeHandler = (e) => {
        ;(async () => {
            const file = e.target.files[0]
            const base64 = await convertToBase64(file)
            createVersionDetailsObj("descriptionImage", base64);
        })()
    }

    const imagesDeleteHandler = (e) => {
        createVersionDetailsObj("descriptionImage", values.bundleGroupForm.standardIcon);
    }

    const statusChangeHandler = (e) => {
        createVersionDetailsObj("status", e.target.value);
        setBundleStatus(e.target.value)
    }

    const descriptionChangeHandler = (e) => {
        setBundleDescriptionLength(e.target.value.length);
        createVersionDetailsObj("description", e.target.value);
        if (e.target.value.length < CHAR_LENGTH) {
            const errorMessageForLengthZeroOrThree = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.descriptionRequired') : i18n.t('formValidationMsg.minDescription')
            validationResult["versionDetails.description"] = [errorMessageForLengthZeroOrThree]
        } else if (e.target.value.length > MAX_CHAR_LENGTH_FOR_DESC) {
            validationResult["versionDetails.description"] = [i18n.t('formValidationMsg.maxDescription')]
        }
    }

    /**
     * Handle keyPress event for input fields and show/hide character limit error message
     * @param {*} e
     */
    const keyPressHandler = (e) => {
        if (e.target.id === NAME_FIELD_ID && e.target.value.length >= MAX_CHAR_LENGTH) {
            validationResult[NAME_FIELD_ID] = [i18n.t('formValidationMsg.max25Char')]
            setShowNameCharLimitErrMsg(true);
            timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
        } else if (e.target.id === DESCRIPTION_FIELD_ID && e.target.value.length >= MAX_CHAR_LENGTH_FOR_DESC) {
            validationResult["versionDetails.description"] = [i18n.t('formValidationMsg.maxDescription')]
            setShowDescriptionCharLimitErrMsg(true);
            timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
        } else if (e.target.id === DOCUMENTATION_FIELD_ID && e.target.value.length >= CHAR_LENGTH_255) {
            validationResult["versionDetails.documentationUrl"] = [i18n.t('formValidationMsg.max255Char')]
            setShowDocUrlCharLimitErrMsg(true);
            timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
        } else if (e.target.id === VERSION_FIELD_ID && e.target.value.length >= CHAR_LENGTH_255) {
            validationResult["versionDetails.version"] = [i18n.t('formValidationMsg.maxVersion255Char')]
            setShowVersionCharLimitErrMsg(true);
            timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
        } else if (e.target.id === CONTACT_URL_FIELD_ID && e.target.value.length >= CHAR_LENGTH_255) {
            validationResult["versionDetails.contactUrl"] = [i18n.t('formValidationMsg.maxVersion255Char')]
            setShowContactUrlCharLimitErrMsg(true);
            timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
        }
    }

    const disappearCharLimitErrMsg = (fieldId) => {
        if (mounted) {
            if (fieldId === NAME_FIELD_ID) {
                validationResult[NAME_FIELD_ID] = undefined;
                setShowNameCharLimitErrMsg(false);
            } else if (fieldId === DESCRIPTION_FIELD_ID) {
                validationResult["versionDetails.description"] = undefined;
                setShowDescriptionCharLimitErrMsg(false);
            } else if (fieldId === DOCUMENTATION_FIELD_ID) {
                validationResult["versionDetails.documentationUrl"] = undefined;
                setShowDocUrlCharLimitErrMsg(false);
            } else if (fieldId === VERSION_FIELD_ID) {
                validationResult["versionDetails.version"] = undefined;
                setShowVersionCharLimitErrMsg(false);
            } else if (fieldId === CONTACT_URL_FIELD_ID) {
                validationResult["versionDetails.contactUrl"] = undefined;
                setShowVersionCharLimitErrMsg(false);
            }
        }
    }

    useEffect(() => {
        setMounted(true);
        // Clear the interval when the component unmounts
        return () => {
            setMounted(false);
            clearTimeout(timerRef.current);
        };
    }, []);

    const onAddOrRemoveBundleFromList = (newBundleList) => {
        createVersionDetailsObj("bundles", newBundleList)
    }

    const handleIsPublicChange = (value) => {
        changeBundleGroup('isPublic', value);
    };

    const shouldDisable = disabled || (!bundleGroup.isEditable && mode === "Edit");
    const versionDetails = bundleGroup && bundleGroup.versionDetails ? bundleGroup.versionDetails : {}
    return (
        <>
            <Content className="Edit-bundle-group">
                <Grid>
                    <Row>
                        <Column sm={16} md={8} lg={8}>
                            <IconUploader
                                descriptionImage={versionDetails.descriptionImage}
                                disabled={disabled}
                                onImageChange={imagesChangeHandler}
                                onImageDelete={imagesDeleteHandler}
                            />
                        </Column>
                    </Row>
                    <Row>
                        <Column sm={16} md={8} lg={{ span: 8, offset: 8 }}>
                            <Checkbox
                                disabled={disabled}
                                id={"isPublic"}
                                labelText={`${i18n.t('component.bundleModalFields.includeInPublicCatalog')}`}
                                checked={!!bundleGroup.isPublic}
                                onChange={handleIsPublicChange}
                            />
                        </Column>
                    </Row>
                    <Row>
                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={(bundleNameLength < CHAR_LENGTH || bundleNameLength > MAX_CHAR_LENGTH || showNameCharLimitErrMsg) && !!validationResult["name"]}
                                invalidText={
                                    (bundleNameLength < CHAR_LENGTH || bundleNameLength > MAX_CHAR_LENGTH || showNameCharLimitErrMsg) ? (validationResult["name"] &&
                                    validationResult["name"].join("; ")) : null
                                }
                                disabled={shouldDisable}
                                value={bundleGroup.name}
                                onChange={nameChangeHandler}
                                maxLength={MAX_CHAR_LENGTH}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
                                onKeyPress={keyPressHandler}
                                id={NAME_FIELD_ID}
                                labelText={`${i18n.t('component.bundleModalFields.name')} ${bundleGroupSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <Select
                                disabled={shouldDisable}
                                value={bundleGroup.categories[0]}
                                onChange={categoryChangeHandler}
                                id={"category"}
                                labelText={`${i18n.t('component.bundleModalFields.category')} ${bundleGroupSchema.fields.categories.exclusiveTests.required ? " *" : ""}`}
                            >
                                {selectItems_Category}
                            </Select>
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={(!isDocumentationAddressValid || showDocUrlCharLimitErrMsg) && !!validationResult["versionDetails.documentationUrl"]}
                                invalidText={
                                    (!isDocumentationAddressValid || showDocUrlCharLimitErrMsg) && (validationResult["versionDetails.documentationUrl"] &&
                                        validationResult["versionDetails.documentationUrl"].join("; "))
                                }
                                disabled={disabled}
                                value={versionDetails.documentationUrl}
                                onChange={documentationChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "documentationUrl")}
                                id={"documentation"}
                                maxLength={CHAR_LENGTH_255}
                                onKeyPress={keyPressHandler}
                                labelText={`${i18n.t('component.bundleModalFields.documentAddress')} ${bundleGroupSchema.fields.documentationUrl.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={(!isBundleVersionValid || showVersionCharLimitErrMsg) && !!validationResult["versionDetails.version"]}
                                invalidText={
                                    (!isBundleVersionValid || showVersionCharLimitErrMsg) && (validationResult["versionDetails.version"] &&
                                        validationResult["versionDetails.version"].join("; "))
                                }
                                disabled={disabled}
                                value={versionDetails.version}
                                onChange={versionChangeHandler}
                                id={VERSION_FIELD_ID}
                                maxLength={CHAR_LENGTH_255}
                                onKeyPress={keyPressHandler}
                                labelText={`${i18n.t('component.bundleModalFields.version')} ${bundleGroupSchema.fields.version.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        {renderOrganisationColumn(bundleGroup.organisationId, orgList)}
                        <Column sm={16} md={16} lg={16}>
                            <Select
                                invalid={!!validationResult["versionDetails.status"]}
                                invalidText={
                                    validationResult["versionDetails.status"] &&
                                    validationResult["versionDetails.status"].join("; ")
                                }
                                disabled={disabled}
                                value={versionDetails.status}
                                onChange={statusChangeHandler}
                                id={"status"}
                                labelText={`${i18n.t('component.bundleModalFields.status')} ${bundleGroupSchema.fields.status.exclusiveTests.required ? " *" : ""}`}>
                                {createSelectOptionsForRoleAndSetSelectStatus}
                            </Select>
                        </Column>

                        <Column sm={16} md={16} lg={16}>
                            <BundlesOfBundleGroup
                                onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}
                                initialBundleList={versionDetails.bundles}
                                disabled={disabled}
                                minOneBundleError={minOneBundleError}
                                displayContactUrl={versionDetails.displayContactUrl}
                                bundleStatus={bundleStatus}
                                mode={mode}
                            />
                        </Column>

                        <Column sm={16} md={16} lg={16}>
                            <Checkbox
                                disabled={disabled}
                                id={"displayContactUrl"}
                                labelText={`${i18n.t('component.bundleModalFields.displayContactLink')}`}
                                checked={versionDetails.displayContactUrl || false}
                                onChange={displayContactChangeHandler}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={(!isContactUrlValid || showContactUrlCharLimitErrMsg) && !!validationResult["versionDetails.contactUrl"]}
                                invalidText={
                                    (!isContactUrlValid || showContactUrlCharLimitErrMsg) && (validationResult["versionDetails.contactUrl"] &&
                                        validationResult["versionDetails.contactUrl"].join("; "))
                                }
                                disabled={disabled || !versionDetails.displayContactUrl}
                                value={versionDetails.contactUrl}
                                onChange={contactUrlChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "contactUrl")}
                                id={"contactUrl"}
                                maxLength={CHAR_LENGTH_255}
                                onKeyPress={keyPressHandler}
                                labelText={`${i18n.t('component.bundleModalFields.contactUrl')} ${versionDetails.displayContactUrl ? " *" : ""}`}
                            />
                        </Column>

                        <Column className="bg-form-textarea" sm={16} md={16} lg={16}>
                            <TextArea
                                invalid={
                                    (bundleDescriptionLength < CHAR_LENGTH || bundleDescriptionLength > MAX_CHAR_LENGTH_FOR_DESC || showDescriptionCharLimitErrMsg) &&
                                    !!validationResult["versionDetails.description"]
                                }
                                invalidText={
                                    (bundleDescriptionLength < CHAR_LENGTH || bundleDescriptionLength > MAX_CHAR_LENGTH_FOR_DESC || showDescriptionCharLimitErrMsg) &&
                                    (validationResult["versionDetails.description"] &&
                                        validationResult["versionDetails.description"].join("; "))
                                }
                                disabled={disabled}
                                value={versionDetails.description}
                                onChange={descriptionChangeHandler}
                                maxLength={MAX_CHAR_LENGTH_FOR_DESC}
                                onKeyPress={keyPressHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
                                id={DESCRIPTION_FIELD_ID}
                                labelText={`${i18n.t('component.bundleModalFields.description')} ${bundleGroupSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
                            />
                            <div className="bg-form-counter bx--label">{versionDetails.description && versionDetails.description.length}/{MAX_CHAR_LENGTH_FOR_DESC}</div>
                        </Column>
                    </Row>
                </Grid>
            </Content>
        </>
    )
}
export default BundleGroupForm
