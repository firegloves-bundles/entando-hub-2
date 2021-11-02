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
import { useState } from 'react'
import BundlesOfBundleGroup from "./update-boundle-group/bundles-of-bundle-group/BundlesOfBundleGroup"
import IconUploader from "./update-boundle-group/icon-uploader/IconUploader"

import "./update-boundle-group/update-bundle-group.scss"
import values from "../../../config/common-configuration";
import { bundleGroupSchema } from "../../../helpers/validation/bundleGroupSchema";
import './bundle-group-form.scss'

const BundleGroupForm = ({
                             bundleGroup,
                             categories,
                             allowedOrganisations, //organisations on which the user can operate
                             onDataChange,
                             selectStatusValues,
                             validationResult,
                         }) => {


    const DESCRIPTION_MAX_LENGTH = 600
    
    const renderOrganisationColumn = (currOrganisationId, organisations) => {
        if(!currOrganisationId) return; //TODO TEMPORARY FIX FOR USERS WITH NO ORGANISATION

        const currOrganisation = organisations.find(o=>o.organisationId===currOrganisationId)

        if (organisations.length === 1) {
            return (<Column sm={16} md={16} lg={16}>
                <TextInput
                    disabled={true}
                    id="organisation"
                    labelText="Organisation"
                    value={currOrganisation.name}
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
                    labelText={"Organisation"}>
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
            <SelectItem key={index} value={curr.value} text={curr.text}/>
        ))

    const selectItems_Category = categories.map((category) => {
        return (
            <SelectItem
                key={category.categoryId}
                value={category.categoryId}
                text={category.name}
            />
        )
    })

    const nameChangeHandler = (e) => {
        changeBundleGroup("name", e.target.value)
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
    }

    const versionChangeHandler = (e) => {
        changeBundleGroup("version", e.target.value)
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
    }

    const descriptionChangeHandler = (e) => {
        changeBundleGroup("description", e.target.value)
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
                                invalid={!!validationResult["name"]}
                                invalidText={
                                    validationResult["name"] &&
                                    validationResult["name"].join("; ")
                                }
                                disabled={disabled}
                                value={bundleGroup.name}
                                onChange={nameChangeHandler}
                                id={"name"}
                                labelText={`Name ${bundleGroupSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <Select
                                disabled={disabled}
                                value={bundleGroup.categories[0]}
                                onChange={categoryChangeHandler}
                                id={"category"}
                                labelText={`Category ${bundleGroupSchema.fields.categories.exclusiveTests.required ? " *" : ""}`}
                            >
                                {selectItems_Category}
                            </Select>
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={!!validationResult["documentationUrl"]}
                                invalidText={
                                    validationResult["documentationUrl"] &&
                                    validationResult["documentationUrl"].join("; ")
                                }
                                disabled={disabled}
                                value={bundleGroup.documentationUrl}
                                onChange={documentationChangeHandler}
                                id={"documentation"}
                                labelText={`Documentation Address ${bundleGroupSchema.fields.documentationUrl.exclusiveTests.required ? " *" : ""}`}
                            />
                        </Column>

                        <Column sm={16} md={8} lg={8}>
                            <TextInput
                                invalid={!!validationResult["version"]}
                                invalidText={
                                    validationResult["version"] &&
                                    validationResult["version"].join("; ")
                                }
                                disabled={disabled}
                                value={bundleGroup.version}
                                onChange={versionChangeHandler}
                                id={"version"}
                                labelText={`Version ${bundleGroupSchema.fields.version.exclusiveTests.required ? " *" : ""}`}
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
                                labelText={`Status ${bundleGroupSchema.fields.status.exclusiveTests.required ? " *" : ""}`}>
                                {createSelectOptionsForRoleAndSetSelectStatus}
                            </Select>
                        </Column>

                        <Column sm={16} md={16} lg={16}>
                            <BundlesOfBundleGroup
                                onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}
                                initialBundleList={bundleGroup.children}
                                disabled={disabled}
                            />
                        </Column>

                        <Column className="bg-form-textarea" sm={16} md={16} lg={16}>
                            <TextArea
                                invalid={!!validationResult["description"]}
                                invalidText={
                                    validationResult["description"] &&
                                    validationResult["description"].join("; ")
                                }
                                disabled={disabled}
                                value={bundleGroup.description}
                                onChange={descriptionChangeHandler}
                                id={"description"}
                                labelText={`Description ${bundleGroupSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
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
