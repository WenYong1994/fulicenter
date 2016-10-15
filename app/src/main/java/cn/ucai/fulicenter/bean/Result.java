package cn.ucai.fulicenter.bean;

public class Result {

    /**
     * retCode : 0
     * retMsg : true
     * retData : {"muserName":"a952702","muserNick":"彭鹏","mavatarId":74,"mavatarPath":"user_avatar","mavatarSuffix":".jpg","mavatarType":0,"mavatarLastUpdateTime":"1476284146171"}
     */

    private int retCode;
    private boolean retMsg;
    /**
     * muserName : a952702
     * muserNick : 彭鹏
     * mavatarId : 74
     * mavatarPath : user_avatar
     * mavatarSuffix : .jpg
     * mavatarType : 0
     * mavatarLastUpdateTime : 1476284146171
     */

    private RetDataBean retData;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public boolean isRetMsg() {
        return retMsg;
    }

    public void setRetMsg(boolean retMsg) {
        this.retMsg = retMsg;
    }

    public RetDataBean getRetData() {
        return retData;
    }

    public void setRetData(RetDataBean retData) {
        this.retData = retData;
    }

    public static class RetDataBean {
        private String muserName;
        private String muserNick;
        private int mavatarId;
        private String mavatarPath;
        private String mavatarSuffix;
        private int mavatarType;
        private String mavatarLastUpdateTime;

        public String getMuserName() {
            return muserName;
        }

        public void setMuserName(String muserName) {
            this.muserName = muserName;
        }

        public String getMuserNick() {
            return muserNick;
        }

        public void setMuserNick(String muserNick) {
            this.muserNick = muserNick;
        }

        public int getMavatarId() {
            return mavatarId;
        }

        public void setMavatarId(int mavatarId) {
            this.mavatarId = mavatarId;
        }

        public String getMavatarPath() {
            return mavatarPath;
        }

        public void setMavatarPath(String mavatarPath) {
            this.mavatarPath = mavatarPath;
        }

        public String getMavatarSuffix() {
            return mavatarSuffix;
        }

        public void setMavatarSuffix(String mavatarSuffix) {
            this.mavatarSuffix = mavatarSuffix;
        }

        public int getMavatarType() {
            return mavatarType;
        }

        public void setMavatarType(int mavatarType) {
            this.mavatarType = mavatarType;
        }

        public String getMavatarLastUpdateTime() {
            return mavatarLastUpdateTime;
        }

        public void setMavatarLastUpdateTime(String mavatarLastUpdateTime) {
            this.mavatarLastUpdateTime = mavatarLastUpdateTime;
        }
    }
}
