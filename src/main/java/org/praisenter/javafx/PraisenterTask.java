/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;

import org.praisenter.javafx.utility.Fx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

/**
 * Represents a task that can be monitored and that has a name and resulting status.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @param <T> the task result type
 */
public abstract class PraisenterTask<T> extends Task<T> {
	/** The task name/description */
	private final String name;
	
	/** The task's result status */
	private final ObjectProperty<PraisenterTaskResultStatus> resultStatus = new SimpleObjectProperty<PraisenterTaskResultStatus>();
	
	private final CyclicBarrier barrier;
	
	/**
	 * Minimal constructor.
	 * @param name the task name or description
	 */
	public PraisenterTask(String name) {
		this(name, null);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the task name or description
	 */
	public PraisenterTask(String name, CyclicBarrier barrier) {
		this.name = name;
		this.barrier = barrier;
		
		InvalidationListener listener = (obs) -> {
			if (this.barrier != null) {
				try {
					this.barrier.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		this.onCancelledProperty().addListener(listener);
		this.onFailedProperty().addListener(listener);
		this.onSucceededProperty().addListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Executes this task on the given executor service.
	 * @param service the service
	 */
	public void execute(ExecutorService service) {
		service.execute(this);
	}
	
	/**
	 * Returns the task name/description.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the result status or null if not complete.
	 * @return {@link PraisenterTaskResultStatus}
	 */
	public PraisenterTaskResultStatus getResultStatus() {
		return this.resultStatus.get();
	}
	
	/**
	 * The result status property.
	 * @return ReadOnlyObjectProperty&lt;{@link PraisenterTaskResultStatus}&gt;
	 */
	public ReadOnlyObjectProperty<PraisenterTaskResultStatus> resultStatusProperty() {
		return this.resultStatus;
	}
	
	/**
	 * Called from sub classes to set the result status.
	 * @param resultStatus the result status
	 */
	protected void setResultStatus(PraisenterTaskResultStatus resultStatus) {
		Fx.runOnFxThead(() -> {
			this.resultStatus.set(resultStatus);
		});
	}
}
