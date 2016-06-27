/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.UI;

import VProvance.Core.Database.DBConnection;
import VProvance.Core.Database.UsefullBatch;
import java.util.List;

/**
 *
 * @author DexpUser
 */
public class ArrivedBatchSource implements IBatchSource{
    
    @Override
    public List<UsefullBatch> GetBatches() {
        return DBConnection.instance().GetArrivedBatches();
    }
}
