import {Button, FileUploader, Row, Column} from "carbon-components-react";
import './icon-uploader.scss';

const IconUploader = ({disabled, descriptionImage, onImageDelete, fileUploaderProps_Images, onImageChange}) => {
    return (
      <>
        <div className="IconUploader">
          <Row>
            {descriptionImage &&
            <div className="IconUploader-image">

              <Column sm={8} md={8} lg={8}>
                <p className="IconUploader-load-title">Upload image</p>
                <p className="IconUploader-load-subtitle">Max file size is
                  500kb. Max
                  4 images. Supported file types are .jpg, .png, and .pdf.</p>
              </Column>
              <Column sm={8} md={8} lg={8}>
                <img src={descriptionImage} alt={""} width="45" height="45"/>
              </Column>

            </div>
            }
            {descriptionImage && !disabled &&
            <div className="IconUploader-delete">
              <Button kind="ghost" onClick={onImageDelete}>Remove</Button>
            </div>
            }
          </Row>
        </div>
        {!disabled && !descriptionImage &&
        <div className="IconUploader-load-image">
          <Row>
            <Column sm={8} md={8} lg={8}>
              <p className="IconUploader-load-title">Upload image</p>
              <p className="IconUploader-load-subtitle">Max file size is 500kb.
                Max
                4 images. Supported file types are .jpg, .png, and .pdf.</p>
            </Column>
          </Row>
          <Row>
            <Column sm={8} md={8} lg={8}>
              <FileUploader onChange={onImageChange}
                            {...fileUploaderProps_Images}/>
            </Column>
          </Row>
        </div>
        }
      </>
  )
}

export default IconUploader
