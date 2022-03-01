public  class Process<T, R> {
    Thread thread;
    SchedulerRunnable<T, R> run;
    

    public Process(SchedulerRunnable<T, R> run) {

        this.run = run;

        thread = new Thread(run);
        thread.start();
    }

    public R getValue() {
        return run.getValue();
    }
}
