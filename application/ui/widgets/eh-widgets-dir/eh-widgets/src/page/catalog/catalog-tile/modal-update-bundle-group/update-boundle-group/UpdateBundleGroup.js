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
import BundlesOfBundleGroup from "./bundles-of-bundle-group/BundlesOfBundleGroup"
import IconUploader from "./icon-uploader/IconUploader"

import "./update-bundle-group.scss"
import values from "../../../../../config/common-configuration";

const UpdateBundleGroup = ({
  bundleGroup,
  categories,
  organisation,
  onDataChange,
  selectStatusValues,
  validationResult,
}) => {
  console.log("UpdateBundleGroup FIRED!", validationResult)

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
                labelText={"Name"}
              />
            </Column>

            <Column sm={16} md={8} lg={8}>
              <Select
                disabled={disabled}
                value={bundleGroup.categories[0]}
                onChange={categoryChangeHandler}
                id={"category"}
                labelText={"Category"}
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
                labelText={"Documentation Address"}
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
                labelText={"Version"}
              />
            </Column>

            <Column sm={16} md={16} lg={16}>
              <TextInput
                disabled={true}
                id="organisation"
                labelText="Organisation"
                value={organisation.name}
              />
            </Column>

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
                labelText={"Status"}
              >
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

            <Column sm={16} md={16} lg={16}>
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
                labelText={"Description"}
              />
            </Column>
          </Row>
        </Grid>
      </Content>
    </>
  )
}
export default UpdateBundleGroup
