import {Column} from "carbon-components-react";
import './icon-uploader.scss';
import values from "../../../../../config/common-configuration";

const IconUploader = ({disabled, descriptionImage, onImageDelete, onImageChange}) => {

    return (
        <div className="IconUploader-image">
            {descriptionImage &&
            <div>
                <img src={descriptionImage} alt={""} width="45" height="45"/>
                {!disabled && descriptionImage !== values.bundleGroupForm.standardIcon && <button className="IconUploader-delete" onClick={onImageDelete}> + </button>}
            </div>
            }
            {!disabled &&
                <Column sm={8} md={8} lg={8}>
                    <div className="bx--form-item" id="images">
                        <p className="bx--file--label"></p>
                        {
            }
                        <label tabIndex="0" className="bx--btn bx--btn--primary bx--btn--sm" htmlFor="id14">
                            <span role="button" aria-disabled="false">Add Files</span>
                        </label>
                        <input className="bx--visually-hidden" id="id14" type="file" tabIndex="-1" accept=""
                               onChange={onImageChange}></input>
                        <div className="bx--file-container">
                        </div>
                    </div>
                </Column>
            }
        </div>
    )
}
export default IconUploader
