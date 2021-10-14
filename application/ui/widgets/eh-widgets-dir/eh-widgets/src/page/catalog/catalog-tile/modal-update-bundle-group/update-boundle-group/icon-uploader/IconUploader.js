

const IconUploader = ({disabled, descriptionImage, onImageDelete, onImageChange}) => {

    return (
        <>
            {descriptionImage &&
            <div>
                <img src={descriptionImage} alt={""} width="45" height="45"/>
                {!disabled && <button onClick={onImageDelete}>X</button>}
            </div>
            }
            {!disabled &&
            <div className="bx--form-item" id="images">
                <p className="bx--file--label"></p>
{/*
                <p className="bx--label-description">Max file size is 500kb. Supported file types are .jpg, .png,
                    and.pdf</p>
*/}
                <label tabIndex="0" className="bx--btn bx--btn--primary bx--btn--sm" htmlFor="id14">
                    <span role="button" aria-disabled="false">Add Files</span>
                </label>
                <input className="bx--visually-hidden" id="id14" type="file" tabIndex="-1" accept="" onChange={onImageChange}></input>
                <div className="bx--file-container"></div>
            </div>
            }
        </>
    )
}
export default IconUploader
