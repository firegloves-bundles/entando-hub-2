import {Button, FileUploader} from "carbon-components-react";

const IconUploader = ({disabled, descriptionImage, onImageDelete, fileUploaderProps_Images, onImageChange}) => {
    return (
        <>
            <div>
                {descriptionImage &&
                <img src={descriptionImage} alt={""} width="45" height="45"/>
                }
                {descriptionImage && !disabled && <Button kind="ghost" onClick={onImageDelete}>Remove</Button>}
            </div>
            {!disabled && !descriptionImage &&
            <FileUploader onChange={onImageChange}
                          {...fileUploaderProps_Images}/>}

        </>
    )
}

export default IconUploader
