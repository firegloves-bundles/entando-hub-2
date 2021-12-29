import { useState } from "react";
import {
    Column,
    Content,
    Grid,
    Row,
    Select,
    SelectItem,
    TextArea,
    TextInput,
} from "carbon-components-react";
import './BundleGroupVersionForm.scss';
import { BUNDLE_STATUS, CHAR_LENGTH, DOCUMENTATION_ADDRESS_URL_REGEX, LEAST_CHAR_NAME_MSG, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC, MAX_CHAR_NAME_MSG, NAME_REQ_MSG, OPERATION, VERSON_REGEX } from "../../../helpers/constants";
import values from "../../../config/common-configuration";
import IconUploader from "../BundleGroupForm/update-boundle-group/icon-uploader/IconUploader";
import { bundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import BundlesOfBundleGroup from "../BundleGroupForm/update-boundle-group/bundles-of-bundle-group/BundlesOfBundleGroup";
import i18n from "../../../i18n";
import { isVersionDuplicate } from "../../../helpers/validation/isVersionDuplicateValidate";

const BundleGroupVersionForm = ({
    bundleGroup,
    categories,
    allowedOrganisations, //organisations on which the user can operate
    onDataChange,
    selectStatusValues,
    validationResult,
    minOneBundleError,
    mode,
    operation,
}) => {

    const [bundleStatus, setBundleStatus] = useState(BUNDLE_STATUS.NOT_PUBLISHED);
    const [bundleNameLength, setBundleNameLength] = useState(0);
    const [bundleDescriptionLength, setBundleDescriptionLength] = useState(0);
    const [isDocumentationAddressValid, setIsDocumentationAddressValid] = useState(false);
    const [isBundleVersionValid, setIsBundleVersionValid] = useState(false);

    const previousVersions = bundleGroup && bundleGroup.allVersions ? bundleGroup.allVersions : [];

    const renderOrganisationColumn = (currOrganisationId, organisations) => {

        if (!currOrganisationId) return; //TODO TEMPORARY FIX FOR USERS WITH NO ORGANISATION

        const currOrganisation = organisations.find(o => Number(o.organisationId) === currOrganisationId);

        if (organisations.length === 1) {
            return (<Column sm={16} md={16} lg={16}>
                <TextInput
                    disabled={true}
                    id="organisation"
                    labelText="Organisation"
                    value={currOrganisation && currOrganisation.name}
                />
            </Column>)
        }
        if (organisations.length > 1) {
            const organisationSelectItems = organisations.map((o) => {
                return (
                    <SelectItem
                        key={o.organisationId}
                        value={o.organisationId}
                        text={o.name}
                    />
                )
            })

            return (<Column sm={16} md={16} lg={16}>
                <Select
                    disabled={operation === OPERATION.ADD_NEW_VERSION ? false : disabled}
                    value={currOrganisation && currOrganisation.organisationId}
                    onChange={organisationChangeHandler}
                    id={"organisation"}
                    labelText={i18n.t('component.bundleModalFields.organisation')}>
                    {organisationSelectItems}
                </Select>
            </Column>)
        }
    }

    const changeBundleGroup = (field, value) => {
        const newObj = {
            ...bundleGroup,
        }
        newObj[field] = value
        onDataChange(newObj)
    }

    const disabled = selectStatusValues.disabled
    const createSelectOptionsForRoleAndSetSelectStatus =
        selectStatusValues.values.map((curr, index) => (
            <SelectItem key={index} value={curr.value} text={curr.text} />
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
            const errorMessageForLengthZeroOrThree = e.target.value.trim().length === 0 ? NAME_REQ_MSG : LEAST_CHAR_NAME_MSG
            validationResult["name"] = [errorMessageForLengthZeroOrThree]
        } else if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
            validationResult["name"] = [MAX_CHAR_NAME_MSG]
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
    }

    const categoryChangeHandler = (e) => {
        changeBundleGroup("categories", [e.target.value])
    }

    const documentationChangeHandler = (e) => {
        changeBundleGroup("documentationUrl", e.target.value)

        setIsValid(e.target.value.trim(), 'documentationUrl')
        if (!e.target.value.trim().length) {
            validationResult["documentationUrl"] = [i18n.t('formValidationMsg.docRequired')]
        } else if (e.target.value.trim().length) {
            validationResult["documentationUrl"] = [i18n.t('formValidationMsg.docFormat')]
        }
    }

    const versionChangeHandler = (e) => {
        changeBundleGroup("version", e.target.value)
        if (!e.target.value.trim().length) {
            validationResult["version"] = [i18n.t('formValidationMsg.versionRequired')]
            setIsBundleVersionValid(false);
        }
        else if (!(e.target.value.trim().length > 0 && new RegExp(VERSON_REGEX).test(e.target.value))) {
            validationResult["version"] = [i18n.t('formValidationMsg.versionFormat')]
            setIsBundleVersionValid(false);
        }
        else if (isVersionDuplicate(e.target.value, bundleGroup.allVersions ? bundleGroup.allVersions : [])) {
            validationResult["version"] = [i18n.t('formValidationMsg.duplicateVersion')]
            setIsBundleVersionValid(false);
        }
        else {
            setIsBundleVersionValid(true);
        }
    }

    const setIsValid = (val, inputTypeName) => {
        if (inputTypeName === 'documentationUrl') {
            val.trim().length > 0 && new RegExp(DOCUMENTATION_ADDRESS_URL_REGEX).test(val) ? setIsDocumentationAddressValid(true) : setIsDocumentationAddressValid(false)
        } else if (inputTypeName === 'version') {
            if (!(previousVersions.includes(val.trim()))) {
                setIsBundleVersionValid(true)
            } else if (val.trim().length > 0 && new RegExp(VERSON_REGEX).test(val)) {
                setIsBundleVersionValid(true)
            } else {
                setIsBundleVersionValid(false);
            }
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
        ; (async () => {
            const file = e.target.files[0]
            const base64 = await convertToBase64(file)
            changeBundleGroup("descriptionImage", base64)
        })()
    }

    const imagesDeleteHandler = (e) => {
        changeBundleGroup("descriptionImage", values.bundleGroupForm.standardIcon)
    }

    const statusChangeHandler = (e) => {
        changeBundleGroup("status", e.target.value)
        setBundleStatus(e.target.value)
    }

    const descriptionChangeHandler = (e) => {
        setBundleDescriptionLength(e.target.value.length);
        changeBundleGroup("description", e.target.value)
        if (e.target.value.length < CHAR_LENGTH) {
            const errorMessageForLengthZeroOrThree = e.target.value.length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
            validationResult["description"] = [errorMessageForLengthZeroOrThree]
        } else if (e.target.value.length > MAX_CHAR_LENGTH_FOR_DESC) {
            validationResult["description"] = [i18n.t('formValidationMsg.maxDescription')]
        }
    }

    const onAddOrRemoveBundleFromList = (newBundleList) => {
        changeBundleGroup("children", newBundleList)
    }
    const isEditableAndNotAddNewVersion = bundleGroup.isEditable && operation !== OPERATION.ADD_NEW_VERSION;
    const disableCondition = (isEditableAndNotAddNewVersion || operation === OPERATION.ADD_NEW_VERSION) ? false : disabled;

    return (
        <>
            <Content className="Edit-bundle-group">
                <Grid>
                    <Row>
                        <Column sm={16} md={8} lg={8}>
                            <IconUploader
                                descriptionImage={bundleGroup.descriptionImage}
                                disabled={disableCondition}
                                onImageChange={imagesChangeHandler}
                                onImageDelete={imagesDeleteHandler}
                            />
                        </Column>
                    </Row>
                    <Row>
                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={(bundleNameLength < CHAR_LENGTH || bundleNameLength > MAX_CHAR_LENGTH) && !!validationResult["name"]}
                                invalidText={
                                    (bundleNameLength < CHAR_LENGTH || bundleNameLength > MAX_CHAR_LENGTH) ? (validationResult["name"] &&
                                        validationResult["name"].join("; ")) : null
                                }
                                disabled={isEditableAndNotAddNewVersion ? false : true}
                                value={bundleGroup.name}
                                onChange={nameChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
                                id={"name"}
                                labelText={`${i18n.t('component.bundleModalFields.name')} ${bundleGroupSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <Select
                                disabled={isEditableAndNotAddNewVersion ? false : true}
                                value={bundleGroup.categories[0]}
                                onChange={categoryChangeHandler}
                                id={"category"}
                                labelText={`${i18n.t('component.bundleModalFields.category')} ${bundleGroupSchema.fields.categories.exclusiveTests.required ? " *" : ""}`}>
                                {selectItems_Category}
                            </Select>
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={!isDocumentationAddressValid && !!validationResult["documentationUrl"]}
                                invalidText={
                                    !isDocumentationAddressValid && (validationResult["documentationUrl"] &&
                                        validationResult["documentationUrl"].join("; "))
                                }
                                disabled={disableCondition}
                                value={bundleGroup && bundleGroup.documentationUrl}
                                onChange={documentationChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "documentationUrl")}
                                id={"documentation"}
                                labelText={`${i18n.t('component.bundleModalFields.documentAddress')} ${bundleGroupSchema.fields.documentationUrl.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={!isBundleVersionValid && !!validationResult["version"]}
                                invalidText={
                                    !isBundleVersionValid && (validationResult["version"] &&
                                        validationResult["version"].join("; "))
                                }
                                disabled={disableCondition}
                                value={bundleGroup && bundleGroup.version}
                                onChange={versionChangeHandler}
                                id={"version"}
                                labelText={`${i18n.t('component.bundleModalFields.version')} ${bundleGroupSchema.fields.version.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        {renderOrganisationColumn(bundleGroup.organisationId, allowedOrganisations)}

                        <Column sm={16} md={16} lg={16}>
                            <Select
                                invalid={!!validationResult["status"]}
                                invalidText={
                                    validationResult["status"] &&
                                    validationResult["status"].join("; ")
                                }
                                disabled={disableCondition}
                                onChange={statusChangeHandler}
                                id={"status"}
                                labelText={`${i18n.t('component.bundleModalFields.status')} ${bundleGroupSchema.fields.status.exclusiveTests.required ? " *" : ""}`}>
                                {createSelectOptionsForRoleAndSetSelectStatus}
                            </Select>
                        </Column>

                        <Column sm={16} md={16} lg={16}>
                            <BundlesOfBundleGroup
                                onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}
                                initialBundleList={bundleGroup.children}
                                disabled={operation === OPERATION.ADD_NEW_VERSION ? false : disabled}
                                minOneBundleError={minOneBundleError}
                                bundleStatus={bundleStatus}
                                mode={mode}
                                operation={operation}
                                bundleGroupIsEditable={bundleGroup.isEditable}
                            />
                        </Column>

                        <Column className="bg-form-textarea" sm={16} md={16} lg={16}>
                            <TextArea
                                invalid={
                                    (bundleDescriptionLength < CHAR_LENGTH || bundleDescriptionLength > MAX_CHAR_LENGTH_FOR_DESC) &&
                                    !!validationResult["description"]
                                }
                                invalidText={
                                    (bundleDescriptionLength < CHAR_LENGTH || bundleDescriptionLength > MAX_CHAR_LENGTH_FOR_DESC) &&
                                    (validationResult["description"] &&
                                        validationResult["description"].join("; "))
                                }
                                disabled={disableCondition}
                                value={bundleGroup && bundleGroup.description}
                                onChange={descriptionChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
                                id={"description"}
                                labelText={`${i18n.t('component.bundleModalFields.description')} ${bundleGroupSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
                            />
                            <div className="bg-form-counter bx--label">{bundleGroup && bundleGroup.description && bundleGroup.description.length}/{MAX_CHAR_LENGTH_FOR_DESC}</div>
                        </Column>
                    </Row>
                </Grid>
            </Content>
        </>
    )
}
export default BundleGroupVersionForm
