package plugings

import plugings.IStepExecutor

interface IContext {
    IStepExecutor getStepExecutor()
}
