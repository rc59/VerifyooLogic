package VerifyooLogic.Statistics.Interfaces;

public abstract class BaseParamAbstract implements IBaseParam {
    public String ParamName;
    public int Weight;

    public int GetWeight() {
        return Weight;
    }
}
