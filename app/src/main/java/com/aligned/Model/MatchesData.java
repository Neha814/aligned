package com.aligned.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 1/11/16.
 */
public class MatchesData {

    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fbId")
    @Expose
    private String fbId;
    @SerializedName("pPic")
    @Expose
    private String pPic;
    @SerializedName("mutualLikecount")
    @Expose
    private Integer mutualLikecount;
    @SerializedName("mutualFriendcout")
    @Expose
    private Integer mutualFriendcout;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("persDesc")
    @Expose
    private String persDesc;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("long")
    @Expose
    private String _long;
    @SerializedName("matchPercentage")
    @Expose
    private String matchPercentage;
    @SerializedName("lastActive")
    @Expose
    private String lastActive;
    @SerializedName("image")
    @Expose
    private List<String> image = new ArrayList<String>();

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The fbId
     */
    public String getFbId() {
        return fbId;
    }

    /**
     *
     * @param fbId
     * The fbId
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    /**
     *
     * @return
     * The pPic
     */
    public String getPPic() {
        return pPic;
    }

    /**
     *
     * @param pPic
     * The pPic
     */
    public void setPPic(String pPic) {
        this.pPic = pPic;
    }

    /**
     *
     * @return
     * The mutualLikecount
     */
    public Integer getMutualLikecount() {
        return mutualLikecount;
    }

    /**
     *
     * @param mutualLikecount
     * The mutualLikecount
     */
    public void setMutualLikecount(Integer mutualLikecount) {
        this.mutualLikecount = mutualLikecount;
    }

    /**
     *
     * @return
     * The mutualFriendcout
     */
    public Integer getMutualFriendcout() {
        return mutualFriendcout;
    }

    /**
     *
     * @param mutualFriendcout
     * The mutualFriendcout
     */
    public void setMutualFriendcout(Integer mutualFriendcout) {
        this.mutualFriendcout = mutualFriendcout;
    }

    /**
     *
     * @return
     * The sex
     */
    public String getSex() {
        return sex;
    }

    /**
     *
     * @param sex
     * The sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     *
     * @return
     * The persDesc
     */
    public String getPersDesc() {
        return persDesc;
    }

    /**
     *
     * @param persDesc
     * The persDesc
     */
    public void setPersDesc(String persDesc) {
        this.persDesc = persDesc;
    }

    /**
     *
     * @return
     * The age
     */
    public String getAge() {
        return age;
    }

    /**
     *
     * @param age
     * The age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     *
     * @return
     * The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The _long
     */
    public String getLong() {
        return _long;
    }

    /**
     *
     * @param _long
     * The long
     */
    public void setLong(String _long) {
        this._long = _long;
    }

    /**
     *
     * @return
     * The matchPercentage
     */
    public String getMatchPercentage() {
        return matchPercentage;
    }

    /**
     *
     * @param matchPercentage
     * The matchPercentage
     */
    public void setMatchPercentage(String matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    /**
     *
     * @return
     * The lastActive
     */
    public String getLastActive() {
        return lastActive;
    }

    /**
     *
     * @param lastActive
     * The lastActive
     */
    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    /**
     *
     * @return
     * The image
     */
    public List<String> getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(List<String> image) {
        this.image = image;
    }


}
