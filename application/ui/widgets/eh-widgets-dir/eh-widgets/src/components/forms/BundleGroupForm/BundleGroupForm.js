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
} from "carbon-components-react"
import BundlesOfBundleGroup from "./update-boundle-group/bundles-of-bundle-group/BundlesOfBundleGroup"
import IconUploader from "./update-boundle-group/icon-uploader/IconUploader"

import "./update-boundle-group/update-bundle-group.scss"
import values from "../../../config/common-configuration";
import { bundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import {
    DOCUMENTATION_ADDRESS_URL_REGEX, VERSON_REGEX, CHAR_LENGTH, MAX_CHAR_LENGTH, NAME_REQ_MSG, LEAST_CHAR_NAME_MSG, 
    MAX_CHAR_NAME_MSG, DESC_REQ_MESG, LEAST_CHAR_DESC_MSG, MAX_CHAR_DESC_MSG, MAX_CHAR_LENGTH_FOR_DESC, DOCUMENTATION_URL_REQ_MSG, DOCUMENTATION_URL_FORMAT_MSG, VERSION_REQ_MSG, VERSION_FORMAT_MSG, BUNDLE_STATUS } from "../../../helpers/constants";
import './bundle-group-form.scss'
import i18n from "../../../i18n";

const BundleGroupForm = ({
                             bundleGroup,
                             categories,
                             allowedOrganisations, //organisations on which the user can operate
                             onDataChange,
                             selectStatusValues,
                             validationResult,
                             minOneBundleError,
                             theBundleStatus,
                             mode
                         }) => {


    const [bundleStatus, setBundleStatus] = useState(theBundleStatus ? theBundleStatus : mode === 'Add' ? BUNDLE_STATUS.NOT_PUBLISHED : "");
    const [bundleNameLength, setBundleNameLength] = useState(0);
    const [bundleDescriptionLength, setBundleDescriptionLength] = useState(0);
    const [isDocumentationAddressValid, setIsDocumentationAddressValid] = useState(false);
    const [isBundleVersionValid, setIsBundleVersionValid] = useState(false);

    const DESCRIPTION_MAX_LENGTH = 600
    
    const renderOrganisationColumn = (currOrganisationId, organisations) => {
        if(!currOrganisationId) return; //TODO TEMPORARY FIX FOR USERS WITH NO ORGANISATION

        const currOrganisation = organisations.find(o=>o.organisationId===currOrganisationId)

        if (organisations.length === 1) {
            return (<Column sm={16} md={16} lg={16}>
                <TextInput
                    disabled={true}
                    id="organisation"
                    labelText={i18n.t('component.bundleModalFields.organisation')}
                    value={currOrganisation.name ? currOrganisation.name : currOrganisation.organisationName}
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
                    disabled={disabled}
                    value={currOrganisation.organisationId}
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
            validationResult["documentationUrl"] = [DOCUMENTATION_URL_REQ_MSG]
        } else if (e.target.value.trim().length) {
            validationResult["documentationUrl"] = [DOCUMENTATION_URL_FORMAT_MSG]
        }
    }

    const versionChangeHandler = (e) => {
        changeBundleGroup("version", e.target.value.trim())
        setIsValid(e.target.value, 'version')
        if (!e.target.value.trim().length) {
            validationResult["version"] = [VERSION_REQ_MSG]
        } else if (e.target.value.trim().length) {
            validationResult["version"] = [VERSION_FORMAT_MSG]
        }
    }

    const setIsValid = (val, inputTypeName) => {
        if (inputTypeName === 'documentationUrl') {
            val.trim().length > 0 && new RegExp(DOCUMENTATION_ADDRESS_URL_REGEX).test(val) ? setIsDocumentationAddressValid(true) : setIsDocumentationAddressValid(false)
        } else if (inputTypeName === 'version') {
            val.trim().length > 0 && new RegExp(VERSON_REGEX).test(val) ? setIsBundleVersionValid(true) : setIsBundleVersionValid(false);
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
            const errorMessageForLengthZeroOrThree = e.target.value.length === 0 ? DESC_REQ_MESG : LEAST_CHAR_DESC_MSG
            validationResult["description"] = [errorMessageForLengthZeroOrThree]
        } else if (e.target.value.length > MAX_CHAR_LENGTH_FOR_DESC) {
            validationResult["description"] = [MAX_CHAR_DESC_MSG]
        }
    }

    const onAddOrRemoveBundleFromList = (newBundleList) => {
        changeBundleGroup("children", newBundleList)
    }

    return (
        <>
            <Content className="Edit-bundle-group">
                <Grid>
                    <Row>
                        <Column sm={16} md={8} lg={8}>
                            <IconUploader
                                descriptionImage={bundleGroup.descriptionImage}
                                disabled={disabled}
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
                                disabled={disabled}
                                value={bundleGroup.name}
                                onChange={nameChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
                                id={"name"}
                                labelText={`${i18n.t('component.bundleModalFields.name')} ${bundleGroupSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <Select
                                disabled={disabled}
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
                                invalid={!isDocumentationAddressValid && !!validationResult["documentationUrl"]}
                                invalidText={
                                    !isDocumentationAddressValid && (validationResult["documentationUrl"] &&
                                        validationResult["documentationUrl"].join("; "))
                                }
                                disabled={disabled}
                                value={bundleGroup.documentationUrl}
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
                                disabled={disabled}
                                value={bundleGroup.version}
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
                                disabled={disabled}
                                value={bundleGroup.status}
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
                                disabled={disabled}
                                minOneBundleError={minOneBundleError}
                                bundleStatus={bundleStatus}
                                mode={mode}
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
                                disabled={disabled}
                                value={bundleGroup.description}
                                onChange={descriptionChangeHandler}
                                onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
                                id={"description"}
                                labelText={`${i18n.t('component.bundleModalFields.description')} ${bundleGroupSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
                            />
                            <div className="bg-form-counter bx--label">{bundleGroup.description.length}/{DESCRIPTION_MAX_LENGTH}</div>
                        </Column>
                    </Row>
                </Grid>
            </Content>
        </>
    )
}
export default BundleGroupForm
