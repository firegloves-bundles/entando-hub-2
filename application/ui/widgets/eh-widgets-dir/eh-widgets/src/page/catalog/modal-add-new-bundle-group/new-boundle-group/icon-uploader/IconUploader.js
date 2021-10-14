import {FileUploader} from "carbon-components-react";

const fileUploaderProps_Images = {
    id: "images",
    buttonLabel: "Add Files",
    filenameStatus: "complete",
    size: "sm",
    labelDescription:
        "Max file size is 500kb. Supported file types are .jpg, .png, and .pdf",
}

const IconUploader = ({disabled, descriptionImage, onImageDelete, onImageChange}) => {

    return (
        <>
            <div>
                {descriptionImage &&
                <img src={descriptionImage} alt={""} width="45" height="45"/>
                }
            </div>
            {!disabled &&
            <FileUploader onChange={onImageChange} onDelete={onImageDelete} {...fileUploaderProps_Images} />}

        </>
    )
}

export default IconUploader
