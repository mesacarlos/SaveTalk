package app.savetalk.db;

public class QueueElement {
    private int ID;
    private String filePath;
    private boolean FTP_UPLOADED;
    private boolean GDRIVE_UPLOADED;
    private boolean CLOUD_UPLOADED;

    public QueueElement(){}

    public QueueElement(int ID, String filePath, boolean FTP_UPLOADED, boolean GDRIVE_UPLOADED, boolean CLOUD_UPLOADED) {
        this.ID = ID;
        this.filePath = filePath;
        this.FTP_UPLOADED = FTP_UPLOADED;
        this.GDRIVE_UPLOADED = GDRIVE_UPLOADED;
        this.CLOUD_UPLOADED = CLOUD_UPLOADED;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFTP_UPLOADED(boolean FTP_UPLOADED) {
        this.FTP_UPLOADED = FTP_UPLOADED;
    }

    public void setGDRIVE_UPLOADED(boolean GDRIVE_UPLOADED) {
        this.GDRIVE_UPLOADED = GDRIVE_UPLOADED;
    }

    public void setCLOUD_UPLOADED(boolean CLOUD_UPLOADED) {
        this.CLOUD_UPLOADED = CLOUD_UPLOADED;
    }

    public int getID() {
        return ID;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isFTP_UPLOADED() {
        return FTP_UPLOADED;
    }

    public boolean isGDRIVE_UPLOADED() {
        return GDRIVE_UPLOADED;
    }

    public boolean isCLOUD_UPLOADED() {
        return CLOUD_UPLOADED;
    }
}