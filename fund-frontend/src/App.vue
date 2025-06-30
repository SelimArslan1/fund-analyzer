<template>
  <div class="app">
    <div class="header">
      <h1>Fund Streak Analyzer</h1>
    </div>

    <div class="container">
      <div class="controls">
        <div class="filters">
          <h3>Filter by Pattern</h3>
          <div class="filter-list">
            <label>
              <input type="checkbox" v-model="filters.price">
              Price
            </label>
            <label>
              <input type="checkbox" v-model="filters.shareCount">
              Share Count
            </label>
            <label>
              <input type="checkbox" v-model="filters.peopleCount">
              People Count
            </label>
            <label>
              <input type="checkbox" v-model="filters.totalPrice">
              Total Price
            </label>
          </div>
        </div>

        <div class="actions">
          <button @click="updateData" :disabled="loading" class="btn btn-update">
            {{ loading && currentAction === 'update' ? 'Updating...' : 'Update' }}
          </button>
          <button @click="analyzeData" :disabled="loading" class="btn btn-analyze">
            {{ loading && currentAction === 'analyze' ? 'Analyzing...' : 'Analyze' }}
          </button>
        </div>
      </div>

      <div v-if="statusMessage" :class="['status', statusMessage.type]">
        {{ statusMessage.text }}
      </div>

      <div class="results">
        <h3>Results ({{ filteredResults.length }})</h3>

        <div v-if="loading && currentAction === 'fetch'" class="loading">
          Loading...
        </div>

        <div v-else-if="filteredResults.length === 0" class="no-results">
          No results found
        </div>

        <div v-else class="fund-list">
          <div v-for="streak in filteredResults" :key="streak.id" class="fund-item">
            <div class="fund-info">
              <div class="fund-code">{{ streak.fundCode }}</div>
              <div class="start-date">{{ formatDate(streak.startDate) }}</div>
            </div>

            <div class="pattern-data">
              <div v-if="streak.pattern.price !== undefined" class="pattern-field">
                <span class="label">Price:</span>
                <span class="value">${{ streak.pattern.price.toFixed(2) }}</span>
              </div>
              <div v-if="streak.pattern.shareCount !== undefined" class="pattern-field">
                <span class="label">Shares:</span>
                <span class="value">{{ streak.pattern.shareCount.toLocaleString() }}</span>
              </div>
              <div v-if="streak.pattern.peopleCount !== undefined" class="pattern-field">
                <span class="label">People:</span>
                <span class="value">{{ streak.pattern.peopleCount.toLocaleString() }}</span>
              </div>
              <div v-if="streak.pattern.totalPrice !== undefined" class="pattern-field">
                <span class="label">Total:</span>
                <span class="value">${{ streak.pattern.totalPrice.toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      fundStreaks: [],
      filteredResults: [],
      filters: {
        price: false,
        shareCount: false,
        peopleCount: false,
        totalPrice: false
      },
      loading: false,
      currentAction: '',
      statusMessage: null
    }
  },

  computed: {
    hasActiveFilters() {
      return Object.values(this.filters).some(filter => filter);
    }
  },

  watch: {
    filters: {
      handler() {
        console.log('Filters changed:', this.filters);
        this.fetchFilteredResults();
      },
      deep: true
    }
  },

  methods: {
    async fetchFundStreaks() {
      this.loading = true;
      this.currentAction = 'fetch';
      this.clearStatus();

      try {
        const response = await fetch('http://localhost:8080/api/fund-streaks');
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        this.fundStreaks = await response.json();
        this.filteredResults = this.fundStreaks;
        this.showStatus('Data loaded successfully', 'success');
      } catch (error) {
        console.error('Error fetching fund streaks:', error);
        this.showStatus('Failed to load data: ' + error.message, 'error');
      } finally {
        this.loading = false;
        this.currentAction = '';
      }
    },

    async fetchFilteredResults() {
      console.log('fetchFilteredResults called');
      console.log('Current filters:', this.filters);
      console.log('Has active filters:', this.hasActiveFilters);

      if (!this.hasActiveFilters) {
        console.log('No active filters, showing all results');
        this.filteredResults = this.fundStreaks;
        return;
      }

      this.loading = true;
      this.currentAction = 'filter';

      try {
        // Build query parameters
        const params = new URLSearchParams();
        if (this.filters.price) params.append('price', 'true');
        if (this.filters.shareCount) params.append('shareCount', 'true');
        if (this.filters.peopleCount) params.append('peopleCount', 'true');
        if (this.filters.totalPrice) params.append('totalPrice', 'true');

        const url = `http://localhost:8080/api/funds/metric?${params.toString()}`;
        console.log('Fetching from:', url);

        const response = await fetch(url);
        console.log('Response status:', response.status);

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const results = await response.json();
        console.log('Filtered results:', results);

        this.filteredResults = results;

      } catch (error) {
        console.error('Error fetching filtered results:', error);
        this.showStatus('Failed to load filtered data: ' + error.message, 'error');
      } finally {
        this.loading = false;
        this.currentAction = '';
      }
    },

    async updateData() {
      this.loading = true;
      this.currentAction = 'update';
      this.clearStatus();

      try {
        const response = await fetch('http://localhost:8080/api/update', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        this.showStatus('Data updated successfully', 'success');
        await this.fetchFundStreaks();
      } catch (error) {
        console.error('Error updating data:', error);
        this.showStatus('Failed to update data: ' + error.message, 'error');
      } finally {
        this.loading = false;
        this.currentAction = '';
      }
    },

    async analyzeData() {
      this.loading = true;
      this.currentAction = 'analyze';
      this.clearStatus();

      try {
        const response = await fetch('http://localhost:8080/api/analyze', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        this.showStatus('Analysis completed successfully', 'success');
        await this.fetchFundStreaks();
      } catch (error) {
        console.error('Error analyzing data:', error);
        this.showStatus('Failed to analyze data: ' + error.message, 'error');
      } finally {
        this.loading = false;
        this.currentAction = '';
      }
    },

    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString();
    },

    showStatus(message, type) {
      this.statusMessage = { text: message, type };
      setTimeout(() => {
        this.statusMessage = null;
      }, 3000);
    },

    clearStatus() {
      this.statusMessage = null;
    }
  },

  mounted() {
    this.fetchFundStreaks();
  }
}
</script>

<style scoped>
.app {
  font-family: Arial, sans-serif;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  color: #333;
  font-size: 2rem;
}

.container {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.controls {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 20px;
}

.filters h3 {
  margin-bottom: 10px;
  color: #555;
}

.filter-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-list label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.filter-list input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

.actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-update {
  background: #28a745;
  color: white;
}

.btn-update:hover:not(:disabled) {
  background: #218838;
}

.btn-analyze {
  background: #fd7e14;
  color: white;
}

.btn-analyze:hover:not(:disabled) {
  background: #e96500;
}

.status {
  padding: 10px;
  margin-bottom: 20px;
  border-radius: 4px;
  font-weight: 500;
}

.status.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.results h3 {
  margin-bottom: 15px;
  color: #333;
}

.loading, .no-results {
  text-align: center;
  color: #666;
  padding: 40px;
}

.fund-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.fund-item {
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 15px;
}

.fund-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.fund-code {
  font-weight: bold;
  font-size: 1.1rem;
  color: #333;
}

.start-date {
  color: #666;
  font-size: 0.9rem;
}

.pattern-data {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.pattern-field {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pattern-field .label {
  color: #666;
  font-size: 0.9rem;
}

.pattern-field .value {
  font-weight: 500;
  color: #333;
}

@media (max-width: 768px) {
  .controls {
    flex-direction: column;
    align-items: stretch;
  }

  .actions {
    justify-content: center;
  }

  .pattern-data {
    flex-direction: column;
    gap: 8px;
  }
}
</style>