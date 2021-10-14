import {useCallback, useEffect, useState} from "react"
import {
  Content,
  Select,
  SelectItem,
  TextArea,
  TextInput,
  Column,
  Grid,
  Row
} from "carbon-components-react"
import BundlesOfBundleGroup from "./bundles-of-bundle-group/BundlesOfBundleGroup"
import IconUploader from "./icon-uploader/IconUploader";

import "./update-bundle-group.scss";
/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}
 */

const UpdateBundleGroup = ({bundleGroup, categories, organisation, children, onDataChange, selectValuesInfo}) => {

  const changeBundleGroup = (field, value) => {
    const newObj = {
      ...bundleGroup,
    }
    newObj[field] = value
    onDataChange(newObj)
  }

  const disabled = selectValuesInfo.disabled
  const createSelectOptionsForRoleAndSetSelectStatus = selectValuesInfo.values.map(
      (curr, index) => <SelectItem key={index} value={curr.value}
                                   text={curr.text}/>)

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

  const categoryChangeHandler = (e) => {
    changeBundleGroup("categories", [e.target.value])
  }

  const documentationChangeHandler = (e) => {
    changeBundleGroup("documentationUrl", e.target.value)
  }

  const versionChangeHandler = (e) => {
    // const value = e.target.value
    // setNewBundleGroup(prev => {
    //   return {
    //     ...prev,
    //     version: value
    //   }
    // })
    //changeNewBundleGroup("version", e.target.value)
  }

  const fileUploaderProps_Images = {
    id: "images",
    buttonLabel: "Add Files",
    labelDescription:
        "Max file size is 500kb. Supported file types are .jpg, .png, and .pdf",
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
    (async () => {
      const file = e.target.files[0]
      const base64 = await convertToBase64(file)
      changeBundleGroup("descriptionImage", base64)
    })()
  }
  const imagesDeleteHandler = (e) => {
    changeBundleGroup("descriptionImage", "")

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
                <IconUploader descriptionImage={bundleGroup.descriptionImage}
                              disabled={disabled}
                              fileUploaderProps_Images={fileUploaderProps_Images}
                              onImageChange={imagesChangeHandler}
                              onImageDelete={imagesDeleteHandler}/>
              </Column>
            </Row>
            <Row>
              <Column sm={16} md={8} lg={8}>
                <TextInput disabled={disabled} value={bundleGroup.name}
                           onChange={nameChangeHandler} id={"name"}
                           labelText={"Name"}/>
              </Column>

              <Column sm={16} md={8} lg={8}>
                <Select disabled={disabled} value={bundleGroup.categories[0]}
                        onChange={categoryChangeHandler}
                        id={"category"}
                        labelText={"Category"}>{selectItems_Category}</Select>
              </Column>

              <Column sm={16} md={8} lg={8}>
                <TextInput disabled={disabled}
                           value={bundleGroup.documentationUrl}
                           onChange={documentationChangeHandler}
                           id={"documentation"}
                           labelText={"Documentation Address"}/>
              </Column>

              <Column sm={16} md={8} lg={8}>
                <TextInput disabled={disabled} value={bundleGroup.version}
                           onChange={versionChangeHandler}
                           id={"version"}
                           labelText={"Version"}/>
              </Column>

              <Column sm={16} md={16} lg={16}>
                <TextInput disabled={true} id="organisation"
                           labelText="Organisation" value={organisation.name}/>
              </Column>

              <Column sm={16} md={16} lg={16}>
                <Select disabled={disabled} value={bundleGroup.status}
                        onChange={statusChangeHandler}
                        id={"status"}
                        labelText={"Status"}>
                  {createSelectOptionsForRoleAndSetSelectStatus}</Select>
              </Column>

              <Column sm={16} md={16} lg={16}>
                <BundlesOfBundleGroup
                    onAddOrRemoveBundleFromList={onAddOrRemoveBundleFromList}
                    initialBundleList={children} disabled={disabled}/>
              </Column>

              <Column sm={16} md={16} lg={16}>
                <TextArea disabled={disabled} value={bundleGroup.description}
                          onChange={descriptionChangeHandler}
                          id={"description"}
                          labelText={"Description"}/>
              </Column>

            </Row>
          </Grid>
        </Content>
      </>
  )
}
export default UpdateBundleGroup
