package com.aligned.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep on 1/11/16.
 */
public class FindMatchData {

    @SerializedName("errNum")
    @Expose
    private String errNum;
    @SerializedName("errFlag")
    @Expose
    private String errFlag;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;
    @SerializedName("matches")
    @Expose
    private List<MatchesData> matches = new ArrayList<MatchesData>();

    /**
     *
     * @return
     * The errNum
     */
    public String getErrNum() {
        return errNum;
    }

    /**
     *
     * @param errNum
     * The errNum
     */
    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    /**
     *
     * @return
     * The errFlag
     */
    public String getErrFlag() {
        return errFlag;
    }

    /**
     *
     * @param errFlag
     * The errFlag
     */
    public void setErrFlag(String errFlag) {
        this.errFlag = errFlag;
    }

    /**
     *
     * @return
     * The errMsg
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     *
     * @param errMsg
     * The errMsg
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    /**
     *
     * @return
     * The matches
     */
    public List<MatchesData> getMatches() {
        return matches;
    }

    /**
     *
     * @param matches
     * The matches
     */
    public void setMatches(List<MatchesData> matches) {
        this.matches = matches;
    }
}
