package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.annotation.PrivateOwned;

@SuppressWarnings("serial")
@Entity
public class DbImage extends CrudModel<DbImage> {

	public static final ModelFinder find = new ModelFinder();

	@Required
	private String filename;
	@Required
	@PrivateOwned
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private RawImage image;
	@Required
	@PrivateOwned
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private RawImage thumbnail;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public RawImage getImage() {
		return image;
	}

	public void setImage(RawImage image) {
		this.image = image;
	}

	public RawImage getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(RawImage thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Override
	public CrudFinder<DbImage> getCrudFinder() {
		return find;
	}

	@Override
	public String getLabel() {
		return getFilename();
	}

	public static class ModelFinder extends CrudFinder<DbImage> {

		public ModelFinder() {
			super(new Finder<Long, DbImage>(Long.class, DbImage.class), "label");
		}

		public DbImage byFilename(String filename) {
			return finder.where().eq("filename", filename).findUnique();
		}
	}

}
