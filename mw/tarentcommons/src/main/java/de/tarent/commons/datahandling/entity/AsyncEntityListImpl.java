package de.tarent.commons.datahandling.entity;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import de.tarent.commons.datahandling.entity.EntityListEvent;

import java.util.*;

import org.apache.commons.logging.Log;

import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.commons.datahandling.entity.EntityListListener;
import de.tarent.commons.logging.LogFactory;
import de.tarent.octopus.client.OctopusCallException;
import de.tarent.commons.datahandling.ListFilter;
import de.tarent.commons.ui.Messages;
import de.tarent.commons.utils.TaskManager;
import de.tarent.commons.datahandling.binding.DataChangedEvent;
import de.tarent.commons.datahandling.binding.AbstractDataSubject;

public class AsyncEntityListImpl extends AbstractDataSubject {

    public final static String PARAM_OFFSET = "offset";
    public final static String PARAM_LIMIT = "limit";
    public final static String PARAM_COLUMN_NAMES = "columnNames";
    public final static String PARAM_FILTER = "filter";

    public final static String KEY_ENTITIES = "entities";
    public final static String KEY_ENTITY_COUNT = "entityCount";

    public static List ATTRIBUTE_LIST_ID = Arrays.asList(new String[] { "id" });

    static final int LOADER_SLEEP_TIME = 15;

    private static final Log logger = LogFactory.getLog(AsyncEntityListImpl.class);

    /**
     * A list of observer (EntityListListeners) for this entity list
     */
    List entityListListeners = new ArrayList(1);

    /**
     * The connection to the octopus instance
     */
    OctopusConnection connection;

    /**
     * The octopus task to call
     */
    String taskName;

    /**
     * The param name for the filter in the octopus task
     */
    String filterParamName;

    /**
     * The stored entities
     */
    ArrayList entities = new ArrayList();

    /**
     * Current field for sorting
     */
    String sortField = null;

    /**
     * Current filter list in reverse polish notation
     */
    List filterList;

    /**
     * List of attributes to load for each entity
     */
    List attributeList;

    /**
     * Count of entities in this selection. -1 means unknown.
     */
    int count = -1;

    /**
     * Amount of entities to load in one go
     */
    int fetchSize = 30;

    /**
     * Maximum value for the limit parameter, before the requests will be splittedo
     */
    int maxLimit = 120;

    /**
     * Maximum queued load Jobs to remember
     */
    int maxQueuedJobs = Integer.MAX_VALUE;

    /**
     * Maximum of objects to store in this list
     */
    int maxHoldEntities = Integer.MAX_VALUE;

    int currentlyHoldEntities = 0;

    /**
     * the last Entity, a client had queried
     */
    int lastRecentlyQueriedEntity = -1;

    /**
     * The loader for the loader thread
     */
    Loader loader = new Loader();

    /**
     * The factory for creation of entity objects
     */
    EntityFactory entityFactory;

    /**
     * Template method to override for error reporting
     */
    protected void reportError(Exception e) {
        e.printStackTrace();
    }

    public void initialLoad() {
        count = -1;
        currentlyHoldEntities = 0;
        lastRecentlyQueriedEntity = -1;
        entities.clear();
        fireEntityListChanged(new EntityListEvent(this, EntityListEvent.UPDATE));
        loader.start();
        // preload the main attributes of the fetchSize first entities
        loadRangeAsync(new LoadJob(0, fetchSize, attributeList));
    }

    public void stop() {
        loader.stopAndLock();
    }

    public void reload() {
        stop();
        initialLoad();
    }

    /**
     * Put the job in a queue,
     * so it can be done by the loader thread.
     */
    public void loadRangeAsync(LoadJob loadJob) {
        loader.loadRangeAsync(loadJob);
    }

    /**
     * Loads a proper block, containing the entity
     */
    protected void loadEntityAsync(final int index) {
        if (!(loader.isLoading(index) || loader.isScheduled(index))) {
            int start = index;
            LoadJob currentJob = loader.getCurrentJob();
            while (start > 0
                    && (currentJob == null || (!currentJob.isBetween(start - 1)))
                    && (entities.size() <= (start - 1) || entities.get(start - 1) == null)
                    && (index - start < getFetchSize() / 2)) {
                start--;
            }

            int fetch = 1;
            while ((currentJob == null || (!currentJob.isBetween(index + fetch)))
                    && (entities.size() <= (index + fetch) || entities.get(index + fetch) == null)
                    && (fetch < getFetchSize() / 2)) {
                fetch++;
            }
            LoadJob newJob = new LoadJob(start, (index - start) + fetch, attributeList);
            loadRangeAsync(newJob);
        }
    }

    /**
     * This method should be called by the loader thread
     * to realy load the entities.
     *
     * TODO: Reporting about job start, error and success to a global instance!
     */
    protected void loadRange(LoadJob loadJob) {
        try {
            if (loadJob.limit > maxLimit) {
                LoadJob deferredJob = new LoadJob(loadJob.offset + maxLimit, loadJob.limit - maxLimit, loadJob.attributes);
                if (logger.isTraceEnabled()) {
                    logger.trace("splitted to large job: " + deferredJob);
                }
                loadRangeAsync(deferredJob);
                loadJob.limit = maxLimit;
            }
            long startTime = 0;
            if (logger.isTraceEnabled()) {
                startTime = System.currentTimeMillis();
                logger.trace("start loading range: " + loadJob);
            }
            OctopusResult res = callTask(loadJob);

            // TODO: error handling for null or wrong type
            List listOfEntities = readEntities(res);
            count = readEntityCount(res);

            int newMinSize = loadJob.offset + Math.min(loadJob.limit, listOfEntities.size());

            synchronized (entities) {
                entities.ensureCapacity(newMinSize);
                while (entities.size() < newMinSize) {
                    entities.add(null);
                }
                int offset = loadJob.offset;

                for (Iterator iter = listOfEntities.iterator(); iter.hasNext(); ) {
                    Object data = iter.next();

                    if (entities.get(offset) == null) {
                        currentlyHoldEntities++;
                    }
                    updatePosition(entities, offset, data);
                    offset++;
                    if (offset >= entities.size() && iter.hasNext()) {
                        logger.warn("The server returned more entities than the client requested.");
                        break;
                    }
                }

                // free some entities. but do not throw away those just fetched +-150
                if (currentlyHoldEntities > (maxHoldEntities * 1.5)) {
                    for (int i = entities.size() - 1; (currentlyHoldEntities > maxHoldEntities) && i >= 0; i--) {
                        if (entities.get(i) != null && Math.abs(lastRecentlyQueriedEntity - i) > 100) {
                            entities.set(i, null);
                            currentlyHoldEntities--;
                        }
                    }
                }
            }
            fireEntityListChanged(new EntityListEvent(this, loadJob.getOffset(), loadJob.getMaxIndex(), EntityListEvent.UPDATE));

            if (logger.isTraceEnabled()) {
                logger.trace("range loaded: " + (System.currentTimeMillis() - startTime) + "ms");
            }
        } catch (Exception e) {
            //TODO: Reporting about job start, error and success to a global instance!
            reportError(e);
        }
    }

    /**
     * This method calls the server side task and returns the OctopusResult with the entity list.
     * Overwrite it to customize the loading behavior.
     */
    protected OctopusResult callTask(LoadJob loadJob) throws OctopusCallException {
        OctopusTask task = connection.getTask(getTaskName());
        // TODO: only query the needed column names

        // force a reload of the filter
        task.add(getFilterParamName() + "." + ListFilter.PARAM_RESET, "true");
        task.add(getFilterParamName() + "." + ListFilter.PARAM_RESET_FILTER, "true");

        task.add(getFilterParamName() + "." + ListFilter.PARAM_START, new Integer(loadJob.offset));
        task.add(getFilterParamName() + "." + ListFilter.PARAM_LIMIT, new Integer(loadJob.limit));
        if (getSortField() != null) {
            task.add(getFilterParamName() + "." + ListFilter.PARAM_SORT_FIELD, getSortField());
        }

        if (getFilterList() != null) {
            task.add(getFilterParamName() + "." + ListFilter.PARAM_FILTER_LIST, getFilterList());
        }

        OctopusResult res = task.invoke();
        return (res);
    }

    /**
     * Returns the List of Entities contained in the octopus result.
     * This method should be overwritten, if needed.
     */
    protected List readEntities(OctopusResult res) {
        return (List) res.getData(KEY_ENTITIES);
    }

    /**
     * Returns total count of Entities. This Information should be in the OctopusResult.
     * This method should be overwritten, if needed.
     */
    protected int readEntityCount(OctopusResult res) {
        Object count = res.getData(KEY_ENTITY_COUNT);
        if (!(count instanceof Integer)) {
            return -1;
        }
        return ((Integer) count).intValue();
    }

    /**
     * This method fills one position in the entity storage list with a new retrieved entity.
     * Overwrite it to customize the update behavior.
     */
    protected void updatePosition(ArrayList entityStorage, int offset, Object data) {
        Map entityData = (Map) data;
        Object entity = entities.get(offset);
        if (entity == null) {
            entity = entityFactory.getEntity(new MapAttributeSource(entityData), null);
            entityStorage.set(offset, entity);
        } else {
            entityFactory.fillEntity(entity, new MapAttributeSource(entityData), null);
        }
    }

    /**
     * If the requested entity is available, it is returned. If it is not available,
     * it will be loaded in the background and null will be returned.
     *
     * @return returns the entity or null, if the entity is not already available
     */
    public Object getEntityAt(int index) {
        if (count != -1 && index >= count) {
            throw new IndexOutOfBoundsException("Wrong index: " + index + " > " + (count - 1));
        }

        Object entity = null;
        synchronized (entities) {
            lastRecentlyQueriedEntity = index;
            // there are three cases:
            // 1. entity available
            // 2. entity not available, but the list is filled with nulls
            // 3. entity not available and the list does not cover the index
            if (entities.size() > index) {
                entity = entities.get(index);
            }
        }

        if (entity == null) {
            loadEntityAsync(index);
        } else {
            int preloadIndex = index + (3 * getFetchSize()) / 4;
            if (preloadIndex < getSize() && (preloadIndex >= entities.size() || entities.get(preloadIndex) == null)) {
                loadEntityAsync(preloadIndex);
            }
        }
        return entity;
    }

    public boolean isSizeKnown() {
        return count != -1;
    }

    public int getSize() {
        if (count < 0) {
            return 0;
        }
        return count;
    }

    public void finalize() {
        loader.stop();
    }

    protected void fireEntityListChanged(EntityListEvent e) {
        for (Iterator iter = entityListListeners.iterator(); iter.hasNext(); ) {
            EntityListListener listener = (EntityListListener) iter.next();
            listener.entityListChanged(e);
        }
        fireDataChanged(new DataChangedEvent(this, ""));
    }

    public void addEntityListListener(EntityListListener listener) {
        entityListListeners.add(listener);
    }

    public void removeEntityListListener(EntityListListener listener) {
        entityListListeners.remove(listener);
    }

    public void removeAllEntityListListeners() {
        entityListListeners.clear();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String newTaskName) {
        this.taskName = newTaskName;
    }

    public String getFilterParamName() {
        return filterParamName;
    }

    public void setFilterParamName(String newFilterParamName) {
        this.filterParamName = newFilterParamName;
    }

    public OctopusConnection getConnection() {
        return connection;
    }

    public void setConnection(OctopusConnection newConnection) {
        this.connection = newConnection;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortField() {
        return sortField;
    }

    public List getFilterList() {
        return filterList;
    }

    public void setFilterList(List newFilterList) {
        this.filterList = newFilterList;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(int newMaxLimit) {
        this.maxLimit = newMaxLimit;
    }

    public int getMaxQueuedJobs() {
        return maxQueuedJobs;
    }

    public void setMaxQueuedJobs(final int newMaxQueuedJobs) {
        this.maxQueuedJobs = newMaxQueuedJobs;
    }

    public int getMaxHoldEntities() {
        return maxHoldEntities;
    }

    public void setMaxHoldEntities(final int newMaxHoldEntities) {
        this.maxHoldEntities = newMaxHoldEntities;
    }

    /**
     * Returns the list of attributes to load for each entity
     */
    public List getAttributeList() {
        return attributeList;
    }

    /**
     * Sets the list of attributes to load for each entity
     */
    public void setAttributeList(List newAttributeList) {
        this.attributeList = newAttributeList;
    }

    /**
     * Returns the amount of entities to load in one go
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * Sets the amount of entities to load in one go
     */
    public void setFetchSize(int newFetchSize) {
        this.fetchSize = newFetchSize;
    }

    /**
     * Returns the factory for creation of entity objects
     */
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    /**
     * sets the factory for creation of entity objects
     */
    public void setEntityFactory(EntityFactory newEntityFactory) {
        this.entityFactory = newEntityFactory;
    }

    public class LoadJob {
        int offset;
        int limit;
        List attributes;

        public LoadJob(int offset, int limit, List attributes) {
            this.offset = offset;
            this.limit = limit;
            this.attributes = attributes;
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }

        public List getAttributes() {
            return attributes;
        }

        /**
         * tests, wether the supplie index is in the range of the load job
         */
        public boolean isBetween(int index) {
            return offset <= index && index <= getMaxIndex();
        }

        public boolean hasSameAttributes(LoadJob otherJob) {
            if (attributes == null && otherJob.attributes == null) {
                return true;
            }
            if (attributes == null || otherJob.attributes == null) {
                return false;
            }
            return attributes.equals(otherJob.attributes);
        }

        public int getMaxIndex() {
            return offset + limit - 1;
        }

        public String toString() {
            return "(" + getOffset() + "," + getMaxIndex() + ")";
        }
    }

    public class Loader implements Runnable {

        /**
         * the loader thread
         */
        Thread thread;

        /**
         * flag, for stopping the runner
         */
        boolean keepRunning = false;

        /**
         * current job
         */
        LoadJob currentJob;

        /**
         * flag, if the loader accepts new jobs
         */
        boolean isLocked = true;

        /**
         * A queue of LoadJob Objects.
         */
        LinkedList jobQueue = new LinkedList();

        /**
         * A status monitor for monitoring the activity
         */
        LoadTaskMonitor loadTaskMonitor;

        /**
         * start the thread, if it is not allready running
         */
        public void start() {
            if (thread == null || !thread.isAlive()) {
                loadTaskMonitor = new LoadTaskMonitor(this);
                keepRunning = true;
                thread = new Thread(this);
                thread.setDaemon(true);
                thread.start();
            }
            isLocked = false;
        }

        public int getQueueSize() {
            synchronized (jobQueue) {
                return jobQueue.size();
            }
        }

        /**
         * Stops the loader, removes all scheduled jobs and and prevents it from loading new jobs.
         */
        public void stopAndLock() {
            if (thread != null && thread.isAlive()) {
                stop();

                if (thread.isAlive()) {
                    // wait max. 1 second.
                    try {
                        thread.join(1000);
                    } catch (InterruptedException ie) {
                        // ignore
                    }
                    // If this is not enough, we interrupt the thread
                    if (thread != null) {
                        thread.interrupt();
                        thread = null;
                    }
                }

            }
            isLocked = true;
            jobQueue.clear();
        }

        public void stop() {
            keepRunning = false;
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        }

        public void cancelJobs() {
            synchronized (jobQueue) {
                if (logger.isTraceEnabled()) {
                    logger.trace("cancel jobs");
                }
                jobQueue.clear();
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            }
        }

        /**
         * Returns true, if the there is already a loadjob covering the supplied index
         */
        public boolean isScheduled(int index) {
            synchronized (jobQueue) {
                for (Iterator iter = jobQueue.iterator(); iter.hasNext(); ) {
                    if (((LoadJob) iter.next()).isBetween(index)) {
                        return true;
                    }
                }
                return false;
            }
        }

        /**
         * returns, if the supplied index ist currently loaded
         */
        public boolean isLoading(int index) {
            synchronized (jobQueue) {
                return (currentJob != null && currentJob.isBetween(index));
            }
        }

        public LoadJob getCurrentJob() {
            return currentJob;
        }

        public void loadRangeAsync(LoadJob newJob) {
            if (isLocked) {
                return;
            }
            synchronized (jobQueue) {

                // try to merge the jobs
                boolean merged = false;
                for (Iterator iter = jobQueue.iterator(); iter.hasNext(); ) {
                    LoadJob job = (LoadJob) iter.next();
                    if (job.isBetween(newJob.offset) && (newJob.limit + job.limit < maxLimit) && job.hasSameAttributes(newJob)) {
                        job.limit = Math.max(job.getMaxIndex(), newJob.getMaxIndex()) - job.offset + 1;
                        merged = true;
                        break;
                    }

                    if (newJob.isBetween(job.offset) && (newJob.limit + job.limit < maxLimit) && job.hasSameAttributes(newJob)) {
                        newJob.limit = Math.max(job.getMaxIndex(), newJob.getMaxIndex()) - newJob.offset + 1;
                        merged = true;
                        break;
                    }
                }

                if (!merged) {
                    jobQueue.addFirst(newJob);
                    while (jobQueue.size() > maxQueuedJobs) {
                        jobQueue.removeLast();
                    }
                    jobQueue.notifyAll();
                    if (!loadTaskMonitor.isRunning()) {
                        TaskManager.getInstance()
                                .register(loadTaskMonitor, Messages.getString("AsyncEntityListImpl_GetAddresses_TaskName"), true);
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace("added load job to queue: " + newJob);
                    }
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("merged load job to queue: " + newJob);
                    }
                }

                // start, if it is not allready running
                start();
            }
        }

        public void run() {
            while (keepRunning) {
                synchronized (jobQueue) {
                    currentJob = null;
                    if (jobQueue.size() > 0) {
                        currentJob = (LoadJob) jobQueue.removeFirst();
                    }
                }
                if (currentJob == null) {
                    try {
                        Thread.sleep(LOADER_SLEEP_TIME);
                    } catch (InterruptedException ie) {
                    }
                } else {
                    loadRange(currentJob);
                }
            }
        }
    }

    /**
     * Simple task implementation for monitoring the loading activity
     */
    public class LoadTaskMonitor implements TaskManager.Task {
        boolean running = false;
        Loader loader;
        int total = 0;
        int done = 0;
        LoadJob lastJob;

        public LoadTaskMonitor(Loader loader) {
            this.loader = loader;
        }

        public boolean isRunning() {
            return running;
        }

        /**
         * @see TaskManager.Task#run()
         */
        public void run(TaskManager.Context ctx) {
            running = true;
            total = 0;
            done = 0;

            while (loader.getQueueSize() > 0 || loader.getCurrentJob() != null) {
                LoadJob cJob = loader.getCurrentJob();
                if (cJob != lastJob) {
                    done++;
                }
                total = loader.getQueueSize() + done;
                if (done >= total) {
                    total = done + 1;
                }

                ctx.setGoal(total);
                ctx.setCurrent(done);
                lastJob = cJob;

                if (cJob != null) {
                    ctx.setActivityDescription(Messages.getFormattedString("AsyncEntityListImpl_GetAddresses_ProgressDesc",
                            new Integer(cJob.getOffset()), new Integer(cJob.getMaxIndex())));
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException ie) {
                }
            }
            running = false;
        }

        /**
         * @see TaskManager.Task#cancel()
         */
        public void cancel() {
            loader.cancelJobs();
        }
    }

}
